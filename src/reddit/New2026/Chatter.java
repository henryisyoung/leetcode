package reddit.New2026;

import java.util.*;

/*
================================================================================
  Chatter — Reddit onsite (2026)
================================================================================

  You are given a stream of chat messages with monotonically-increasing IDs
  (the IDs behave like timestamps: unique, arriving in order). Implement a
  `Chatter` with the following API, then answer 5 follow-up questions.

  Given (from the prompt)
    - No duplicate IDs.
    - Every incoming chat_message arrives in ID order across ALL `load` calls
      (i.e. the last message of load[k] has a smaller ID than the first
      message of load[k+1]).

  Core API
    load(messages)      append a batch; may be called many times
    save()              snapshot all currently-known messages, in ID order
    getMessages(id)     return [2 before, self, 2 after], clamped at edges
    getMulti(ids)       union of getMessages(id) for each id, sorted by ID,
                        DEDUPLICATED (windows may overlap)
    edit(id, content)   (Q4) mutate the content of an existing message
                        without changing its ID or ordering

  Followups (mapped to the interviewer's 5 questions)
    Q1  getMessages     — window of ±2 around a target ID.
    Q2  getMulti        — sort inputs, union windows, iterate in order.
    Q3  read caching    — two HashMap caches (single + multi) with correct
                          invalidation on load/edit.
    Q4  edit            — with mutable content the storage layout doesn't
                          change; only cache invalidation gets slightly more
                          interesting (affects the ±2 neighbors of the edit).
    Q5  history         — see `VersionedChatter` below; each message keeps
                          an append-only version list, `getMessages` returns
                          the current version, `getHistory(id)` returns all.

  Data layout
    List<Message>            messages       — chronological, matches ID order
    Map<Double, Integer>     idx            — id -> position in `messages`
    Map<Double, List<Message>>       single — Q3 cache for getMessages
    Map<List<Double>, List<Message>> multi  — Q3 cache for getMulti
                                              (key is sorted-ids list)

  Complexity   (n = total stored messages, W = window radius = 2, k = 2W+1 = 5)
    Q0 load(b):     O(b) amortized                 append b items + b idx puts
                    + O(W) cache invalidation cost   (last 2 single entries)
                    + O(#multi_keys) for multi.clear()
    Q0 save():      O(n)                           full snapshot copy
    Q1 getMessages: O(1) miss (idx lookup + slice of ≤ k), then O(k) copy;
                    cache hit is O(1) — returns the cached list reference.
    Q2 getMulti(m): O(m log m) sort/dedup input
                    + O(m + total-window-size ≤ m·k) union-of-ranges emit;
                    cache hit is O(1).
    Q3 caches:      hit path O(1); invalidation cost documented under Q0/Q4.
    Q4 edit(id):    O(1) content mutation
                    + O(k) invalidation of single[id-W .. id+W]
                    + O(#multi_keys) multi.clear().
    Q5 versioned:
       load, edit               O(1) amortized (append to versions list)
       getMessages(id)          O(k) copy of window
       getHistory(id)           O(v) where v = # versions of that id
       memory                   O(n + Σ edits) — never shrinks; use pruning
                                (retain last-N versions) if bounded is needed.
    Memory (base): O(n) storage + O(cache) — cache size bounded by number of
                   live get_messages / get_multi keys since the last mutation.

  Cache invalidation rules (the interesting part)
    load(new)       — old messages at index N-1 and N-2 (last 2 before this
                      load) had windows clipped at the old tail; their after
                      neighbors have changed. Invalidate those two single
                      cache entries. `multi` cache: clear (any prior get_multi
                      output could now include additional trailing neighbors).
    edit(i)         — every message j with |j - i| <= 2 has i in its window,
                      so its cached window still references the edited object,
                      but the content moved. If Message.content is mutable
                      the LIST reference is fine but the cached snapshot of
                      that content is stale. Invalidate single[j] for
                      j in [i-2, i+2] (clamped). `multi`: clear.

    (Simpler alternative: don't keep a separate `multi` cache — derive
     getMulti from the `single` cache. That reduces invalidation surface at
     the cost of one extra sort + union per multi call.)
================================================================================
*/
public class Chatter {

    /** A single chat message. Content is mutable to support `edit`. */
    public static class Message {
        public final double id;
        public final int version = 0;
        public String content;
        public Message(double id, String content) { this.id = id; this.content = content; }
        @Override public String toString() { return "{" + id + ": \"" + content + "\"}"; }
        @Override public boolean equals(Object o) {
            if (!(o instanceof Message)) return false;
            Message m = (Message) o;
            return Double.compare(id, m.id) == 0 && Objects.equals(content, m.content);
        }
        @Override public int hashCode() { return Objects.hash(id, content); }
    }

    private static final int WINDOW = 2;

    private final List<Message>         messages = new ArrayList<>();
    private final Map<Double, Integer>  idx      = new HashMap<>();

    // Q3 caches — invalidated on any mutation (see rules in header).
    private final Map<Double, List<Message>>        single = new HashMap<>();
    private final Map<List<Double>, List<Message>>  multi  = new HashMap<>();

    /* ------------------------------ Q0: load / save ------------------------------ */

    /** Time O(b + W + |multi|) — append b items, invalidate last W single entries, clear multi. */
    public void load(List<Message> batch) {
        if (batch == null || batch.isEmpty()) return;
        int oldN = messages.size();
        for (Message m : batch) {
            idx.put(m.id, messages.size());
            messages.add(m);
        }
        // last 2 messages before this load had after-windows clipped at the old tail;
        // their windows have now grown.
        for (int i = Math.max(0, oldN - WINDOW); i < oldN; i++) {
            single.remove(messages.get(i).id);
        }
        multi.clear();
    }

    /** Time O(n) — full snapshot copy. */
    public List<Message> save() {
        return new ArrayList<>(messages);
    }

    /* ------------------------------ Q1: getMessages ------------------------------ */

    /** Time O(1) on cache hit; O(k) on miss (k = window size ≤ 5) — idx lookup + subList copy. */
    public List<Message> getMessages(double id) {
        List<Message> hit = single.get(id);
        if (hit != null) return hit;

        Integer i = idx.get(id);
        if (i == null) return Collections.emptyList();   // unknown id — defensible default

        int lo = Math.max(0, i - WINDOW);
        int hi = Math.min(messages.size(), i + WINDOW + 1);
        List<Message> out = new ArrayList<>(messages.subList(lo, hi));
        single.put(id, out);
        return out;
    }

    /* ------------------------------ Q2: getMulti --------------------------------- */

    /** Time O(m log m + m·k) on miss (m ids, k = 5); O(1) on cache hit. */
    public List<Message> getMulti(List<Double> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();

        List<Double> sortedIds = new ArrayList<>(new TreeSet<>(ids));   // sorted + deduped
        List<Message> hit = multi.get(sortedIds);
        if (hit != null) return hit;

        // Union of window indices from every input id, then emit in index order.
        TreeSet<Integer> take = new TreeSet<>();
        for (Double id : sortedIds) {
            Integer i = idx.get(id);
            if (i == null) continue;
            int lo = Math.max(0, i - WINDOW);
            int hi = Math.min(messages.size(), i + WINDOW + 1);
            for (int j = lo; j < hi; j++) take.add(j);
        }

        List<Message> out = new ArrayList<>(take.size());
        for (int j : take) out.add(messages.get(j));
        multi.put(sortedIds, out);
        return out;
    }

    /* ------------------------------ Q4: edit ------------------------------------- */

    /** Time O(k + |multi|) — mutate + drop ±W single entries + multi.clear. */
    public void edit(double id, String newContent) {
        Integer i = idx.get(id);
        if (i == null) throw new NoSuchElementException("no message with id=" + id);
        messages.get(i).content = newContent;

        // Invalidate any single-cache entry whose window includes index i.
        int lo = Math.max(0, i - WINDOW);
        int hi = Math.min(messages.size(), i + WINDOW + 1);
        for (int j = lo; j < hi; j++) single.remove(messages.get(j).id);
        multi.clear();
    }

    /* ============================== Q5: history-aware variant ================== */
    /*
     * `VersionedChatter` keeps every past content string for every id. The
     * storage change is small: swap the mutable String on Message for an
     * append-only List<String>. Reads still see the LATEST version; a new
     * `getHistory(id)` returns the full version list oldest→newest.
     *
     * Why this shape:
     *   - preserves the O(1) getMessages / edit primitives (only add to a list)
     *   - keeps IDs stable (order in the chat log doesn't change on edit)
     *   - version list is per-message so cold history is not scanned on reads
     *   - simple to extend to "restore version k" by appending versions[k]
     *     as a new latest version (never mutate history in place)
     */
    public static class VersionedMessage {
        public final double id;
        public final List<String> versions = new ArrayList<>();
        public VersionedMessage(double id, String initial) {
            this.id = id;
            this.versions.add(initial);
        }
        /** Time O(1) — tail of the versions list. */
        public String current() { return versions.get(versions.size() - 1); }
    }

    public static class VersionedChatter {
        private final List<VersionedMessage> log = new ArrayList<>();
        private final Map<Double, Integer>   idx = new HashMap<>();

        /** Time O(b) — append b entries and index them. */
        public void load(List<VersionedMessage> batch) {
            for (VersionedMessage m : batch) { idx.put(m.id, log.size()); log.add(m); }
        }

        /** Time O(k) — copy the ±W window (k ≤ 5). */
        public List<VersionedMessage> getMessages(double id) {
            int i = idx.get(id);
            int lo = Math.max(0, i - WINDOW);
            int hi = Math.min(log.size(), i + WINDOW + 1);
            return new ArrayList<>(log.subList(lo, hi));
        }

        /** Time O(1) amortized — append to a per-message versions list. */
        public void edit(double id, String newContent) {
            int i = idx.get(id);
            log.get(i).versions.add(newContent);
        }

        /** Q5 API: full version list for one id, oldest → newest. Time O(v) — copy of v versions. */
        public List<String> getHistory(double id) {
            int i = idx.get(id);
            return new ArrayList<>(log.get(i).versions);
        }
    }

    /* ============================== Tests / demo =============================== */

    public static void main(String[] args) {
        List<Message> batch1 = Arrays.asList(
                m(123.41, "Hello Snoo"),
                m(123.43, "Very nice to meet"),
                m(123.45, "you."),
                m(123.47, "Hope you had a wonderful"),
                m(124.48, "time so far."),
                m(124.52, "At Reddit, you'll help"),
                m(125.53, "build something that encourages"),
                m(126.56, "millions around the world to think more"),
                m(126.57, "do more, learn more, feel more and maybe even laugh more."),
                m(128.61, "Our mission is to bring community"),
                m(129.62, "and belonging to everyone in the world")
        );
        List<Message> batch2 = Arrays.asList(
                m(130.65, "Our core value is to make something people love"),
                m(132.67, "Hope you"),
                m(134.68, "enjoyed this interview"),
                m(135.53, "session very much"),
                m(135.71, "and had loads of fun"),
                m(135.73, "along the way"),
                m(135.75, "as much as we did")
        );

        Chatter c = new Chatter();
        c.load(batch1);

        // Q1: left-edge — no 2 before, only self + 2 after.
        expect("Q1 left edge",
                c.getMessages(123.41),
                Arrays.asList(m(123.41, "Hello Snoo"),
                              m(123.43, "Very nice to meet"),
                              m(123.45, "you.")));

        // Q1: middle — full ±2 window (125.53 is at index 6).
        expect("Q1 interior",
                c.getMessages(125.53),
                Arrays.asList(m(124.48, "time so far."),
                              m(124.52, "At Reddit, you'll help"),
                              m(125.53, "build something that encourages"),
                              m(126.56, "millions around the world to think more"),
                              m(126.57, "do more, learn more, feel more and maybe even laugh more.")));

        // Q1: right edge (before second load) — clamped.
        expect("Q1 right edge before load",
                c.getMessages(129.62),
                Arrays.asList(m(126.57, "do more, learn more, feel more and maybe even laugh more."),
                              m(128.61, "Our mission is to bring community"),
                              m(129.62, "and belonging to everyone in the world")));

        c.load(batch2);

        // After load: 129.62 was at the tail; its window should now extend.
        expect("Q3 cache invalidated on load",
                c.getMessages(129.62),
                Arrays.asList(m(126.57, "do more, learn more, feel more and maybe even laugh more."),
                              m(128.61, "Our mission is to bring community"),
                              m(129.62, "and belonging to everyone in the world"),
                              m(130.65, "Our core value is to make something people love"),
                              m(132.67, "Hope you")));

        // Q2: exactly the prompt's expected output.
        expect("Q2 get_multi",
                c.getMulti(Arrays.asList(128.61, 130.65)),
                Arrays.asList(m(126.56, "millions around the world to think more"),
                              m(126.57, "do more, learn more, feel more and maybe even laugh more."),
                              m(128.61, "Our mission is to bring community"),
                              m(129.62, "and belonging to everyone in the world"),
                              m(130.65, "Our core value is to make something people love"),
                              m(132.67, "Hope you"),
                              m(134.68, "enjoyed this interview")));

        // Q2: unordered input still produces sorted, deduplicated output.
        expect("Q2 unordered input",
                c.getMulti(Arrays.asList(130.65, 128.61, 128.61)),
                c.getMulti(Arrays.asList(128.61, 130.65)));

        // Q3: cache hit path — calling again should return the exact same list.
        List<Message> a = c.getMessages(129.62);
        List<Message> b = c.getMessages(129.62);
        System.out.println((a == b ? "OK   " : "FAIL ") + "Q3 single cache hit returns cached instance");

        // Q4: edit mutates content, invalidates ±2 windows.
        c.edit(129.62, "and belonging — everyone in the world");
        expect("Q4 edit visible in window",
                c.getMessages(129.62),
                Arrays.asList(m(126.57, "do more, learn more, feel more and maybe even laugh more."),
                              m(128.61, "Our mission is to bring community"),
                              m(129.62, "and belonging — everyone in the world"),
                              m(130.65, "Our core value is to make something people love"),
                              m(132.67, "Hope you")));
        // A neighbor's window should also reflect the edit.
        expect("Q4 edit visible in neighbor window",
                c.getMessages(128.61).stream()
                        .filter(msg -> Double.compare(msg.id, 129.62) == 0)
                        .findFirst().orElseThrow().content,
                "and belonging — everyone in the world");

        // Q5: history-aware variant retains every past version.
        VersionedChatter vc = new VersionedChatter();
        vc.load(Arrays.asList(new VersionedMessage(1.0, "typo hallo"),
                              new VersionedMessage(2.0, "world")));
        vc.edit(1.0, "hello");
        vc.edit(1.0, "Hello!");
        expect("Q5 history preserves all versions",
                vc.getHistory(1.0),
                Arrays.asList("typo hallo", "hello", "Hello!"));
        expect("Q5 getMessages returns current version",
                vc.getMessages(1.0).get(0).current(),
                "Hello!");
    }

    /* --------------------------- helpers --------------------------- */

    private static Message m(double id, String content) { return new Message(id, content); }

    private static <T> void expect(String label, T got, T expected) {
        boolean ok = Objects.equals(got, expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + (ok ? "" : "\n  got     =" + got + "\n  expected=" + expected));
    }
}
