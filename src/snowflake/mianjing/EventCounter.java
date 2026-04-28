package snowflake.mianjing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
Problem
-------
Events of various types stream in. Implement two operations:

    receive(eventType, timestamp)  →  void
        Register that one event of `eventType` occurred at `timestamp`.
        You may assume timestamps for receive() are non-decreasing.

    count(eventType, startTime, endTime)  →  int
        Return how many events of `eventType` happened in
        the inclusive interval [startTime, endTime].

Approach
--------
Group timestamps by event type. Because incoming timestamps are
non-decreasing, each per-type list is automatically sorted, so range counts
become two binary searches:

    count = upperBound(end) - lowerBound(start)

where lowerBound(t) is the first index ≥ t (so values < start are excluded)
and upperBound(t) ≡ lowerBound(t + 1) is the first index > t (so values
> end are excluded). The difference is the number of items in the closed
interval [start, end].

Time
----
    receive : O(1) amortized
    count   : O(log n_type)            n_type = events of that type

Space
-----
    O(N) total — one timestamp slot per event ever received.
 */
public class EventCounter {

    private final Map<String, List<Long>> events = new HashMap<>();

    public void receive(String eventType, long timestamp) {
        events.computeIfAbsent(eventType, k -> new ArrayList<>()).add(timestamp);
    }

    public int count(String eventType, long startTime, long endTime) {
        if (startTime > endTime) return 0;
        List<Long> list = events.get(eventType);
        if (list == null || list.isEmpty()) return 0;

        int lo = lowerBound(list, startTime);          // first i with ts[i] >= startTime
        int hi = lowerBound(list, endTime + 1);        // first i with ts[i] >= endTime + 1
        return hi - lo;
    }

    /** Returns the smallest index i such that list.get(i) >= target,
     *  or list.size() if no such index exists. */
    private int lowerBound(List<Long> list, long target) {
        int lo = 0, hi = list.size();
        while (lo < hi) {
            int mid = (lo + hi)/ 2;
            if (list.get(mid) < target) lo = mid + 1;
            else                        hi = mid;
        }
        return lo;
    }

    // ============================================================
    // Demo / tests
    // ============================================================
    public static void main(String[] args) {
        EventCounter c = new EventCounter();

        c.receive("login",  10);
        c.receive("login",  20);
        c.receive("logout", 25);
        c.receive("login",  30);
        c.receive("login",  30);          // duplicate timestamp, same type → both counted
        c.receive("logout", 40);

        // Inclusive interval covers all three "login" early events.
        check(c.count("login", 10, 30), 4, "login [10,30]");

        // start strictly after first login.
        check(c.count("login", 11, 30), 3, "login [11,30]");

        // end strictly before duplicate logins at 30.
        check(c.count("login", 10, 29), 2, "login [10,29]");

        // Type that exists, range with no matches.
        check(c.count("login", 100, 200), 0, "login [100,200]");

        // Type that never occurred.
        check(c.count("never_seen", 0, 1000), 0, "never_seen [0,1000]");

        // Single-point query.
        check(c.count("login", 30, 30), 2, "login [30,30] (duplicates)");

        // Mixed types.
        check(c.count("logout", 0, 100), 2, "logout [0,100]");
        check(c.count("logout", 26, 39), 0, "logout [26,39]");

        // start > end → defensive 0.
        check(c.count("login", 50, 10), 0, "login [50,10] (inverted)");

        System.out.println("\nAll EventCounter checks passed.");
    }

    private static void check(int got, int expected, String label) {
        boolean ok = got == expected;
        System.out.println(label + ": " + got + (ok ? "  OK" : "  FAIL (expected " + expected + ")"));
        if (!ok) throw new AssertionError(label);
    }
}
