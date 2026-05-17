package waymo;
/*
Count Super Streaks From Event Logs (with optional user_id follow-up)

A player is in a streak as long as they keep performing the same event_type
repeatedly AND the time gap between consecutive actions stays within max_gap.

  events[i] = (timestamp, event_type)         (and, in the follow-up, user_id)

  A streak is a maximal contiguous subsequence (after sort-by-time) such that
    - all events share the same event_type, AND
    - for every adjacent pair, timestamp[i] - timestamp[i-1] <= max_gap.

  A "Super Streak" is a streak whose length is >= min_len.

Task
  Return the number of Super Streaks in events.

Follow-up
  If every event also carries user_id, return per-user counts.  Sort/group
  by (user_id, timestamp) first, then run the same logic on each user's slice.

Stdin format
  Line 1:        n max_gap min_len
  Next n lines:  timestamp event_type        (one event per line)
  Output:        a single integer (the Super Streak count)

Example
  Input
    6 5 3
    0 A
    3 A
    9 A
    10 A
    12 A
    14 A
  Output
    1
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/*
Algorithm: single pass over events sorted by time.

  Walk the sorted list keeping a running streak length and the (lastTs,
  lastType) of the most recent event.  For each event e:
    - if it extends the current streak (same type AND gap <= maxGap):
        streakLen++
    - otherwise:
        if streakLen >= minLen: count++         // close out the previous streak
        streakLen = 1                            // start a fresh streak at e

  After the loop, close out the final streak the same way.

Why "close on transition + once at the end":
  A streak is maximal by definition, so it ends exactly when we encounter an
  event that does NOT extend it — that's the natural close-out point — plus
  the implicit end of input.  Each event is visited once.

Complexity
  Time:  O(n log n) for the sort + O(n) for the scan.  Pre-sorted input → O(n).
  Memory: O(n) for the sorted copy (or O(1) extra if sort in place).

Follow-up (per-user)
  Group by user_id, sort each group by timestamp, run the same scan per group.
  Total work is still O(n log n) — sorting each group is dominated by the
  combined input size.
*/
public class CountSuperStreaks2 {

    /** One log entry.  {@code userId} is null when not provided. */
    public static final class Event {
        public final long timestamp;
        public final String type;
        public final String userId;

        public Event(long timestamp, String type) {
            this(timestamp, type, null);
        }

        public Event(long timestamp, String type, String userId) {
            this.timestamp = timestamp;
            this.type = type;
            this.userId = userId;
        }

        @Override
        public String toString() {
            return "(" + timestamp + ", " + type + (userId == null ? "" : ", " + userId) + ")";
        }
    }

    /* --------------------------- Single-user (no userId) --------------------------- */

    public long countSuperStreaks(List<Event> events, long maxGap, int minLen) {
        if (events == null || events.isEmpty()) return 0;
        List<Event> sorted = new ArrayList<>(events);
        // Stable sort by timestamp keeps deterministic order for same-timestamp events.
        sorted.sort(Comparator.comparingLong(e -> e.timestamp));
        return countSorted(sorted, maxGap, minLen);
    }

    /* --------------------------- Follow-up (per-user) --------------------------- */

    public Map<String, Long> countSuperStreaksPerUser(List<Event> events, long maxGap, int minLen) {
        Map<String, Long> out = new LinkedHashMap<>();
        if (events == null || events.isEmpty()) return out;

        Map<String, List<Event>> byUser = new LinkedHashMap<>();
        for (Event e : events) {
            byUser.computeIfAbsent(e.userId, k -> new ArrayList<>()).add(e);
        }
        for (Map.Entry<String, List<Event>> entry : byUser.entrySet()) {
            List<Event> slice = entry.getValue();
            slice.sort(Comparator.comparingLong(e -> e.timestamp));
            out.put(entry.getKey(), countSorted(slice, maxGap, minLen));
        }
        return out;
    }

    /* --------------------------- Shared scanner --------------------------- */

    /** Assumes {@code sorted} is already sorted by ascending timestamp. */
    private long countSorted(List<Event> sorted, long maxGap, int minLen) {
        long count = 0;
        long lastTime = 0;
        long strike = 0;
        String lastEvent = "";

        for (Event event : sorted) {
            boolean eventCont = strike > 0
                    && event.type.equals(lastEvent)
                    && event.timestamp - lastTime <= maxGap;
            if (eventCont) {
                strike++;
            } else {
                if (strike >= minLen) count++;
                strike = 1;
            }
            lastEvent = event.type;
            lastTime = event.timestamp;
        }

        if (strike >= minLen) count++;

        return count;
    }

    /* --------------------------- IO --------------------------- */

    public static void main(String[] args) throws IOException {
        if (args.length == 0 && hasStdin()) {
            runFromStdin();
            return;
        }
        runDemos();
    }

    private static boolean hasStdin() {
        try {
            return System.in.available() > 0;
        } catch (IOException e) {
            return false;
        }
    }

    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        long maxGap = Long.parseLong(st.nextToken());
        int minLen = Integer.parseInt(st.nextToken());

        List<Event> events = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            StringTokenizer line = new StringTokenizer(br.readLine());
            long ts = Long.parseLong(line.nextToken());
            String type = line.nextToken();
            events.add(new Event(ts, type));
        }
        System.out.println(new CountSuperStreaks2().countSuperStreaks(events, maxGap, minLen));
    }

    /* --------------------------- Demo + tests --------------------------- */

    private static void runDemos() {
        CountSuperStreaks2 solver = new CountSuperStreaks2();

        // Spec example.
        check(solver, eventsOf(
                e(0, "A"), e(3, "A"), e(9, "A"), e(10, "A"), e(12, "A"), e(14, "A")
        ), 5, 3, 1);

        // All same type, all within gap → 1 super streak of length n.
        check(solver, eventsOf(
                e(0, "A"), e(1, "A"), e(2, "A"), e(3, "A"), e(4, "A")
        ), 5, 3, 1);

        // All same type but every gap exceeds max_gap → 5 streaks of length 1, none super.
        check(solver, eventsOf(
                e(0, "A"), e(10, "A"), e(20, "A"), e(30, "A"), e(40, "A")
        ), 5, 3, 0);

        // min_len = 1 → every streak counts.  Here there are 3 streaks (A x2 / B x1 / A x2).
        check(solver, eventsOf(
                e(0, "A"), e(1, "A"), e(2, "B"), e(3, "A"), e(4, "A")
        ), 5, 1, 3);

        // Type changes mid-stream — closes the current streak even if gap is fine.
        check(solver, eventsOf(
                e(0, "A"), e(1, "A"), e(2, "A"), e(3, "B"), e(4, "B"), e(5, "B")
        ), 10, 3, 2);

        // Empty input.
        check(solver, eventsOf(), 5, 1, 0);

        // Single event: streak length 1.
        check(solver, eventsOf(e(0, "A")), 5, 1, 1);
        check(solver, eventsOf(e(0, "A")), 5, 2, 0);

        // Unsorted input must be sorted first.
        check(solver, eventsOf(
                e(14, "A"), e(0, "A"), e(12, "A"), e(3, "A"), e(10, "A"), e(9, "A")
        ), 5, 3, 1);

        // Same-timestamp events (gap 0 ≤ max_gap) — three same-type events at t=5 form a streak.
        check(solver, eventsOf(
                e(5, "A"), e(5, "A"), e(5, "A")
        ), 0, 3, 1);

        // Two streaks of exactly min_len length.
        check(solver, eventsOf(
                e(0, "A"), e(1, "A"), e(2, "A"),
                e(100, "A"), e(101, "A"), e(102, "A")  // gap to previous block > max_gap → new streak
        ), 5, 3, 2);

        // ---------- Follow-up: per-user ----------
        List<Event> multi = Arrays.asList(
                // Alice: one super streak of A's, then one isolated B.
                new Event(0,  "A", "alice"),
                new Event(2,  "A", "alice"),
                new Event(4,  "A", "alice"),
                new Event(50, "B", "alice"),

                // Bob: two B-streaks each of length 3 (separated by a big gap).
                new Event(0,   "B", "bob"),
                new Event(1,   "B", "bob"),
                new Event(2,   "B", "bob"),
                new Event(100, "B", "bob"),
                new Event(101, "B", "bob"),
                new Event(102, "B", "bob"),

                // Carol: type changes break the streak — no super streak.
                new Event(0, "A", "carol"),
                new Event(1, "B", "carol"),
                new Event(2, "A", "carol"),
                new Event(3, "B", "carol")
        );
        Map<String, Long> per = solver.countSuperStreaksPerUser(multi, /*maxGap*/5, /*minLen*/3);
        System.out.println("\nPer-user counts (maxGap=5, minLen=3):");
        System.out.println("  " + per);
        Map<String, Long> expected = new LinkedHashMap<>();
        expected.put("alice", 1L);
        expected.put("bob", 2L);
        expected.put("carol", 0L);
        System.out.println("  " + (per.equals(expected) ? "OK" : "FAIL — expected " + expected));

        // ---------- Cross-check the scanner against an O(n^2) brute force ----------
        long mismatches = 0;
        long seed = 17;
        java.util.Random rnd = new java.util.Random(seed);
        for (int t = 0; t < 300; t++) {
            int n = rnd.nextInt(15);                        // 0..14 events
            long maxGap = rnd.nextInt(8);                   // 0..7
            int minLen = 1 + rnd.nextInt(5);                // 1..5
            List<Event> evs = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                long ts = rnd.nextInt(40);
                String type = "T" + rnd.nextInt(3);
                evs.add(new Event(ts, type));
            }
            long a = solver.countSuperStreaks(evs, maxGap, minLen);
            long b = solver.countSuperStreaksBrute(evs, maxGap, minLen);
            if (a != b) {
                mismatches++;
                System.out.println("MISMATCH maxGap=" + maxGap + " minLen=" + minLen
                        + " events=" + evs + " got=" + a + " brute=" + b);
            }
        }
        System.out.println("Random cross-check: " + (300 - mismatches) + "/300 ok");
    }

    /* --------------------------- Brute reference for tests --------------------------- */

    /** O(n^2) brute: enumerate every maximal streak and count. */
    long countSuperStreaksBrute(List<Event> events, long maxGap, int minLen) {
        if (events == null || events.isEmpty()) return 0;
        List<Event> s = new ArrayList<>(events);
        s.sort(Comparator.comparingLong(e -> e.timestamp));
        long count = 0;
        int i = 0;
        while (i < s.size()) {
            int j = i + 1;
            while (j < s.size()
                    && s.get(j).type.equals(s.get(j - 1).type)
                    && s.get(j).timestamp - s.get(j - 1).timestamp <= maxGap) {
                j++;
            }
            if (j - i >= minLen) count++;
            i = j;
        }
        return count;
    }

    /* --------------------------- Tiny helpers for terse demo wiring --------------------------- */

    private static List<Event> eventsOf(Event... evs) {
        return new ArrayList<>(Arrays.asList(evs));
    }

    private static Event e(long ts, String type) {
        return new Event(ts, type);
    }

    private static void check(CountSuperStreaks2 solver, List<Event> events,
                              long maxGap, int minLen, long expected) {
        long got = solver.countSuperStreaks(events, maxGap, minLen);
        long brute = solver.countSuperStreaksBrute(events, maxGap, minLen);
        boolean ok = got == expected && brute == expected;
        System.out.println((ok ? "OK   " : "FAIL ")
                + "n=" + events.size() + " maxGap=" + maxGap + " minLen=" + minLen
                + " expected=" + expected + " scan=" + got + " brute=" + brute
                + "  events=" + events);
    }
}
