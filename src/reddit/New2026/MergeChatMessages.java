package reddit.New2026;

import java.util.*;

/*
================================================================================
  MergeChatMessages — Reddit onsite (2026), Merge-K variant + versioning
================================================================================

  GIVEN
    An existing backend method
        List<Message> get_chat_messages(long id)
    returning a window of 5 messages centered on `id`  (2 before, self, 2 after)
    sorted ascending by message id. Each list itself is dedup'd but different
    calls will overlap.

  IMPLEMENT
        List<Message> merge_messages(long... ids)
    that merges every window into a single, id-ascending, DEDUP'd list.
    Explicit constraint from the interviewer:  ❗ 不能直接用 Set.

    So we can't just dump everything into a HashSet/TreeSet. Two clean routes:

      A. Heap merge (this file)
         Treat the K windows as K sorted lists → classic Merge-K-Lists.
         Min-heap of cursors keyed by (message id). When we poll, if the id
         equals the last-emitted id we drop it and advance the cursor. No Set,
         only adjacency check on a sorted stream.
         Time  O(N log K)     Space  O(K)   for N total messages across K lists.

      B. Two-pointer merge repeated K−1 times
         Merge list[0] with list[1] into an accumulator, then accumulator with
         list[2], and so on. Same adjacency-dedup on each pair.
         Time  O(N·K)         Space  O(N)
         Simpler, but slower for many windows. Fine for K = 2.

  FOLLOW-UP  — messages support editing; how do we store versions and how does
              merge behave when the same id shows up at different versions?

    Design
      Store  Map<msgId, List<Message>> versions   (append-only; last = current)
      Each Message carries {id, version, content}.
      `edit(id, content)` appends versions[id].size() as the new version #.
      `get_chat_messages(id)` returns the LATEST version of each window entry.

      Note: no wall-clock/logical timestamp field is needed — `version` is a
      per-id monotonic counter and merge tie-breaks only compare versions
      within the same id. Add an `editedAt` back only if UI / audit / time-
      window queries need it.

    Merge with versions
      Two windows fetched at slightly different times can disagree on the
      version of the same message id (one call saw v=0, the other v=1).
      Tie-break in the heap comparator:
          same id → HIGHER version wins → we keep the freshest edit
                                          and still emit each id exactly once.

    Cheaper alternatives if we only need "the latest snapshot"
      - Store only the latest Message per id  →  Map<Long, Message>
        (plus an audit log if we still need history).
      - Copy-on-write / immutable-linked-list of versions for time-travel reads.

  --------------------------------------------------------------------------
  Data layout   (mirrors Chatter's List + index-map shape)
  --------------------------------------------------------------------------
    List<List<Message>>   log     — position i → append-only version list for
                                    the i-th message (chronological order)
    Map<Long, Integer>    idx     — id → position in `log`
    long                  nextId  — auto-incrementing id counter

    We don't need TreeMap navigation: get_chat_messages only needs positional
    windowing (±W around one index), never id-range queries. A List+index-map
    gives O(1) lookup and O(W) window build.

  --------------------------------------------------------------------------
  Complexity   (K = # input ids, N = total messages across all K windows
                before dedup, W = window radius = 2, n = # stored messages)
  --------------------------------------------------------------------------
    append(content)               O(1)       list.add + map.put
    edit(id, content)             O(1)       map.get + list.add (amortized)
    getHistory(id)                O(1)       map.get + unmodifiable view
    get_chat_messages(id)         O(W)       positional window slice
    merge_messages(ids...)        O(K · W)   window fetches
                                  + O(N log K) heap merge
    mergeSortedLists(lists)       O(N log K) heap-of-cursors merge, O(K) heap
    Two-pointer alt (K−1 merges)  O(N * K) time, O(N) space
================================================================================
*/
public class MergeChatMessages {

    /** A single chat message. Immutable — edit appends a new version instead of mutating. */
    public static final class Message {
        public final long   id;
        public final int    version;      // 0 for original, +1 per edit
        public final String content;

        public Message(long id, int version, String content) {
            this.id = id; this.version = version; this.content = content;
        }
        @Override public String toString() { return "#" + id + "@v" + version + "(" + content + ")"; }
        @Override public boolean equals(Object o) {
            if (!(o instanceof Message)) return false;
            Message m = (Message) o;
            return id == m.id && version == m.version && Objects.equals(content, m.content);
        }
        @Override public int hashCode() { return Objects.hash(id, version, content); }
    }

    private static final int WINDOW = 2;

    private final List<List<Message>> log    = new ArrayList<>();     // pos → version list
    private final Map<Long, Integer>  idx    = new HashMap<>();       // id  → pos in `log`
    private long                      nextId = 1;

    public MergeChatMessages() {}

    /* ------------------------------ Q0: append / edit ------------------------------ */

    /** Time O(1) — one list.add + one map.put. Returns the new message's id. */
    public long append(String content) {
        long id = nextId++;
        List<Message> versions = new ArrayList<>();
        versions.add(new Message(id, 0, content));
        idx.put(id, log.size());
        log.add(versions);
        return id;
    }

    /** Follow-up: append a new version. Returns the new version number, or -1 if id unknown.
     *  Time O(1) amortized — map.get + list.add. */
    public int edit(long id, String content) {
        Integer pos = idx.get(id);
        if (pos == null) return -1;
        List<Message> versions = log.get(pos);
        int v = versions.size();
        versions.add(new Message(id, v, content));
        return v;
    }

    /* ------------------------------ Reads ------------------------------ */

    /** Time O(1) — map.get; returns an unmodifiable view (no copy). */
    public List<Message> getHistory(long id) {
        Integer pos = idx.get(id);
        return pos == null ? Collections.emptyList() : Collections.unmodifiableList(log.get(pos));
    }

    /** The "given" backend API: 2 before, self, 2 after, id-ascending, latest version of each.
     *  Time O(W) — index lookup + positional slice, mirrors Chatter.getMessages. */
    public List<Message> get_chat_messages(long id) {
        Integer pos = idx.get(id);
        if (pos == null) return Collections.emptyList();

        int lo = Math.max(0, pos - WINDOW);
        int hi = Math.min(log.size(), pos + WINDOW + 1);
        List<Message> out = new ArrayList<>(hi - lo);
        for (int i = lo; i < hi; i++) out.add(latestOf(log.get(i)));
        return out;
    }

    private static Message latestOf(List<Message> versions) {
        return versions.get(versions.size() - 1);
    }

    /* ------------------------------ Q1: merge_messages ------------------------------ */

    /** Merge every get_chat_messages(id) window into a single id-ascending dedup'd list.
     *  Time O(K · W) window fetches + O(N log K) heap merge. */
    public List<Message> merge_messages(long... ids) {
        List<List<Message>> windows = new ArrayList<>();
        for (long id : ids) {
            List<Message> w = get_chat_messages(id);
            if (!w.isEmpty()) windows.add(w);
        }
        return mergeSortedLists(windows);
    }

    /** Heap cursor into one of the input lists — carries the current message inline
     *  so the comparator doesn't need to re-index `lists` on every compare. */
    private static final class Cursor {
        final int     listIdx;
        final int     msgIdx;
        final Message msg;
        Cursor(int listIdx, int msgIdx, Message msg) {
            this.listIdx = listIdx; this.msgIdx = msgIdx; this.msg = msg;
        }
    }

    /**
     * K-way merge of already-sorted-by-id message lists with adjacency dedup.
     * On id ties, the entry with the HIGHER version wins (freshest edit).
     * Time O(N log K), Space O(K) heap — N = total messages across K lists.
     */
    public static List<Message> mergeSortedLists(List<List<Message>> lists) {
        PriorityQueue<Cursor> pq = new PriorityQueue<>((a, b) -> {
            if (a.msg.id != b.msg.id) return Long.compare(a.msg.id, b.msg.id);
            return Integer.compare(b.msg.version, a.msg.version);   // higher version pops first
        });
        for (int i = 0; i < lists.size(); i++) {
            List<Message> l = lists.get(i);
            if (!l.isEmpty()) pq.offer(new Cursor(i, 0, l.get(0)));
        }

        List<Message> out = new ArrayList<>();
        long lastId = Long.MIN_VALUE;
        boolean seen = false;
        while (!pq.isEmpty()) {
            Cursor cur = pq.poll();

            if (!seen || cur.msg.id != lastId) {                    // adjacency dedup — no Set
                out.add(cur.msg);
                lastId = cur.msg.id;
                seen = true;
            }
            int next = cur.msgIdx + 1;
            List<Message> l = lists.get(cur.listIdx);
            if (next < l.size()) {
                pq.offer(new Cursor(cur.listIdx, next, l.get(next)));
            }
        }
        return out;
    }

    /* ============================== Tests / demo =============================== */

    public static void main(String[] args) {
        MergeChatMessages c = new MergeChatMessages();
        for (int i = 1; i <= 10; i++) c.append("m" + i);

        // --- get_chat_messages sanity ---
        expect("window(3) has ids 1..5",
                idsOf(c.get_chat_messages(3)), Arrays.asList(1L, 2L, 3L, 4L, 5L));
        expect("window(1) is left-clipped 1..3",
                idsOf(c.get_chat_messages(1)), Arrays.asList(1L, 2L, 3L));
        expect("window(10) is right-clipped 8..10",
                idsOf(c.get_chat_messages(10)), Arrays.asList(8L, 9L, 10L));

        // --- merge_messages: overlap on id=5 ---
        expect("merge(3,7) covers 1..9 once",
                idsOf(c.merge_messages(3L, 7L)),
                Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L));

        // --- merge_messages: three windows spanning full range ---
        expect("merge(2,5,9) covers all 10",
                idsOf(c.merge_messages(2L, 5L, 9L)),
                Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L));

        // --- Follow-up: version tie-break picks freshest edit ---
        c.edit(5L, "m5-edited");                                  // v=1
        List<Message> merged = c.merge_messages(3L, 7L);
        Message m5 = merged.stream().filter(m -> m.id == 5L).findFirst().orElseThrow();
        expect("edited id=5 shows up at v=1 after merge", m5.version, 1);
        expect("content is the edited one",              m5.content, "m5-edited");

        // --- Direct mergeSortedLists with hand-crafted version conflict ---
        // list A saw id=5 at v=0 (stale snapshot), list B saw v=1.  Higher version wins.
        List<Message> stale = Arrays.asList(
                msg(4, 0, "m4"), msg(5, 0, "m5"), msg(6, 0, "m6"));
        List<Message> fresh = Arrays.asList(
                msg(5, 1, "m5*"), msg(6, 0, "m6"), msg(7, 0, "m7"));
        expect("version conflict picks v=1",
                mergeSortedLists(Arrays.asList(stale, fresh)),
                Arrays.asList(msg(4, 0, "m4"), msg(5, 1, "m5*"),
                              msg(6, 0, "m6"), msg(7, 0, "m7")));

        // --- Edge cases ---
        expect("merge() with no ids",     c.merge_messages(),                     Collections.emptyList());
        expect("merge with missing id",   idsOf(c.merge_messages(999L)),          Collections.emptyList());
        expect("merge single id delegates to window",
                idsOf(c.merge_messages(4L)),
                idsOf(c.get_chat_messages(4L)));

        // --- History preserved after edit ---
        expect("history(5) has 2 versions after 1 edit",
                c.getHistory(5L).size(), 2);
    }

    /* --------------------------- helpers --------------------------- */

    private static Message msg(long id, int v, String content) { return new Message(id, v, content); }

    private static List<Long> idsOf(List<Message> ms) {
        List<Long> out = new ArrayList<>(ms.size());
        for (Message m : ms) out.add(m.id);
        return out;
    }

    private static <T> void expect(String label, T got, T expected) {
        boolean ok = Objects.equals(got, expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + (ok ? "" : "\n  got     =" + got + "\n  expected=" + expected));
    }
}
