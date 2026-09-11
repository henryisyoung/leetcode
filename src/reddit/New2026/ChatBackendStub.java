package reddit.New2026;

import java.util.*;

import reddit.New2026.MergeChatMessages.Message;

/*
================================================================================
  ChatBackendStub — how "you are given get_chat_messages(id)" actually works
================================================================================

  MergeChatMessages implements get_chat_messages itself, which is convenient for
  a single-file test but hides the point of the question. In the room the
  interviewer says "assume this exists" and your job is merge_messages. So the
  API is a DEPENDENCY, not something you own:

      given     interface ChatBackend { List<Message> getChatMessages(long id); }
      yours     List<Message> mergeMessages(ChatBackend api, long... ids)

  Writing it this way costs one parameter and buys three things: your merge is
  testable before any backend exists, you can count the calls you make to it,
  and you cannot accidentally reach around the API into storage you were never
  given.

  --------------------------------------------------------------------------
  ⚠ ASK ABOUT THE CONTRACT. This is most of the signal in the question.
  --------------------------------------------------------------------------
  Every one of these changes your solution, and none of them is stated:

    1. Is the returned list sorted by id?
       The whole heap merge assumes it. If not, you are sorting K lists first
       and the complexity story changes. Demonstrated below: an unsorted
       window makes mergeSortedLists emit duplicates.

    2. Exactly 5, or clipped at the ends?
       Clipped, here. If it pads or wraps, your boundary tests are wrong.

    3. Unknown id — empty list, null, or throw?
       A null is the one that NPEs your for-each. Guard it or pin it down.

    4. Can one call return the same id twice?
       If yes, adjacency dedup inside a single list matters too.

    5. Is each call a fresh snapshot?
       If yes, two windows can disagree about the version of the same id —
       which is exactly the versioning follow-up. Higher version wins.

    6. Is it a network call?
       Then call count IS the cost, not the merge. Dedupe the input ids
       (below), and expect "can you batch or parallelise these?" next.

  --------------------------------------------------------------------------
  Complexity   (K distinct ids, W = window radius, N = K·(2W+1) messages)
  --------------------------------------------------------------------------
    mergeMessages     K distinct backend calls + O(N log K) merge
                      Deduping the input ids is what makes it K DISTINCT
                      rather than K, which matters the moment the call is
                      remote. Everything else is MergeChatMessages.
================================================================================
*/
public class ChatBackendStub {

    /** GIVEN by the interviewer. You call it; you never implement it. */
    public interface ChatBackend {
        /** A window of 2W+1 messages centred on `id`, ascending by id, latest version of each. */
        List<Message> getChatMessages(long id);
    }

    /* ------------------------- YOUR code ------------------------- */

    /** Depends on the interface only — it cannot see how anything is stored. */
    public static List<Message> mergeMessages(ChatBackend api, long... ids) {
        Set<Long> distinct = new LinkedHashSet<>();
        for (long id : ids) distinct.add(id);            // one backend call per DISTINCT id

        List<List<Message>> windows = new ArrayList<>();
        for (long id : distinct) {
            List<Message> w = api.getChatMessages(id);
            if (w != null && !w.isEmpty()) windows.add(w);
        }
        return MergeChatMessages.mergeSortedLists(windows);
    }

    /* ----------------------- test doubles ----------------------- */

    /** A stand-in for the real backend so the merge can be run before one exists. */
    public static final class FakeChatBackend implements ChatBackend {
        private final int window;
        private final List<Long> order = new ArrayList<>();          // position -> id
        private final Map<Long, Message> latest = new HashMap<>();

        public FakeChatBackend(int window) { this.window = window; }

        public FakeChatBackend seed(long id, String content) {
            order.add(id);
            latest.put(id, new Message(id, 0, content));
            return this;
        }

        public FakeChatBackend edit(long id, String content) {
            Message cur = latest.get(id);
            if (cur == null) throw new NoSuchElementException("no message " + id);
            latest.put(id, new Message(id, cur.version + 1, content));
            return this;
        }

        @Override
        public List<Message> getChatMessages(long id) {
            int pos = order.indexOf(id);                             // O(n); it is a stub
            if (pos < 0) return Collections.emptyList();             // contract question 3
            List<Message> out = new ArrayList<>();
            for (int i = Math.max(0, pos - window); i < Math.min(order.size(), pos + window + 1); i++)
                out.add(latest.get(order.get(i)));
            return out;
        }
    }

    /** Decorator that answers "how many backend calls did you make?" with a number. */
    public static final class CountingChatBackend implements ChatBackend {
        private final ChatBackend delegate;
        private final List<Long> calls = new ArrayList<>();

        public CountingChatBackend(ChatBackend delegate) { this.delegate = delegate; }

        @Override public List<Message> getChatMessages(long id) {
            calls.add(id);
            return delegate.getChatMessages(id);
        }
        public int callCount()   { return calls.size(); }
        public List<Long> calls() { return List.copyOf(calls); }
    }

    /** A backend that violates contract question 1, to show why you have to ask. */
    public static final class UnsortedChatBackend implements ChatBackend {
        private final ChatBackend delegate;
        public UnsortedChatBackend(ChatBackend delegate) { this.delegate = delegate; }
        @Override public List<Message> getChatMessages(long id) {
            List<Message> out = new ArrayList<>(delegate.getChatMessages(id));
            Collections.reverse(out);
            return out;
        }
    }

    /* ============================== demo ============================== */

    public static void main(String[] args) {
        FakeChatBackend fake = new FakeChatBackend(2);
        for (int i = 1; i <= 10; i++) fake.seed(i, "m" + i);

        CountingChatBackend api = new CountingChatBackend(fake);

        expect("merge(3,7) covers 1..9 once", ids(mergeMessages(api, 3L, 7L)),
                Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L));
        expect("two distinct ids, two backend calls", api.callCount(), 2);

        CountingChatBackend dup = new CountingChatBackend(fake);
        expect("repeated ids give the same answer", ids(mergeMessages(dup, 3L, 7L, 3L, 7L, 3L)),
                Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L));
        expect("...but still only two backend calls", dup.callCount(), 2);

        expect("unknown id yields nothing", ids(mergeMessages(api, 999L)), Collections.emptyList());
        expect("no ids, no calls", mergeMessages(new CountingChatBackend(fake)), Collections.emptyList());

        // Contract question 5: an edit between calls, and the merge keeps the fresher one.
        fake.edit(5L, "m5-edited");
        Message m5 = mergeMessages(api, 3L, 7L).stream()
                .filter(m -> m.id == 5L).findFirst().orElseThrow();
        expect("edited id 5 merges at v1", m5.version, 1);
        expect("with the edited content",  m5.content, "m5-edited");

        // Contract question 1: the same data, unsorted, silently breaks the merge.
        List<Message> broken = mergeMessages(new UnsortedChatBackend(fake), 3L, 7L);
        System.out.println("\nif the backend does NOT sort by id, adjacency dedup fails:");
        System.out.println("  ids out = " + ids(broken));
        System.out.println("  " + ids(broken).size() + " entries for 9 distinct ids"
                + " -- this is why question 1 is worth asking");
    }

    private static List<Long> ids(List<Message> ms) {
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
