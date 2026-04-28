package snowflake.mianjing;

import java.util.Arrays;
import java.util.PriorityQueue;

/*
LeetCode 630 — Course Schedule III
----------------------------------
n online courses, courses[i] = [duration_i, lastDay_i].
Each course must be taken contiguously for duration_i days and must finish by
lastDay_i. We start on day 1 and can only take one course at a time.

Return the maximum number of courses that can be completed.

Example
-------
  courses = [[100,200],[200,1300],[1000,1250],[2000,3200]]
  Answer: 3
    Take course 0 (100 days, end day 100)
    Take course 1 (200 days, end day 300)
    Take course 3 (2000 days, end day 2300, ≤ 3200)
    (Course 2 doesn't fit — 1000 days would push us past day 1250.)


Greedy + max-heap (the standard idea)
-------------------------------------
Sort courses by deadline (lastDay) ascending. Walk them in deadline order
and maintain a "currently-scheduled" set in a max-heap keyed by duration.

For each course (d, end):
    1. Tentatively schedule it: time += d; push d into the heap.
    2. If time > end (we've blown the deadline by adding this course),
       evict the LONGEST-duration course currently in the heap:
           time -= heap.pop()
       That eviction may pop the course we just added (if it's the longest);
       that's fine — we tried to take it but kicking out a shorter one wouldn't
       have helped.

Why this is optimal
-------------------
Process courses in deadline order. At each step the question is: "can I do
this course on top of what I've already committed to?"  If yes, take it.  If
no, swap it in only when it's strictly *better* than something already
committed — and "better" means *shorter* duration, because shorter durations
free up time for future courses without changing the count.

The heap-pop-on-overflow trick implements exactly that swap: when adding the
new course breaks the budget, we discard whichever scheduled course (possibly
the new one) has the longest duration. The heap's size after each step is the
maximum number of courses completable using only courses seen so far while
respecting all deadlines processed.

Time:  O(n log n) for the sort + heap ops.
Space: O(n) for the heap.
 */
public class CourseSchedule3 {

    public int scheduleCourse(int[][] courses) {
        if (courses == null || courses.length == 0) return 0;

        Arrays.sort(courses, (a, b) -> Integer.compare(a[1], b[1]));

        // Max-heap of durations of courses we've tentatively scheduled.
        PriorityQueue<Integer> heap = new PriorityQueue<>((a, b) -> Integer.compare(b, a));
        int time = 0;

        for (int[] c : courses) {
            int duration = c[0], end = c[1];
            time += duration;
            heap.offer(duration);
            if (time > end) {
                // We just blew the deadline. Drop the longest scheduled course.
                time -= heap.poll();
            }
        }
        return heap.size();
    }

    // ---------------------------------------------------------------------
    // Demo / tests
    // ---------------------------------------------------------------------
    public static void main(String[] args) {
        CourseSchedule3 s = new CourseSchedule3();

        // Spec example: 3.
        check(s.scheduleCourse(new int[][]{
                {100, 200}, {200, 1300}, {1000, 1250}, {2000, 3200}}), 3, "spec");

        // No course fits past its own deadline (single course longer than its end).
        check(s.scheduleCourse(new int[][]{{5, 4}}), 0, "single overlong course");

        // Single course that fits exactly on its deadline.
        check(s.scheduleCourse(new int[][]{{5, 5}}), 1, "single exact-fit");

        // Two short courses both deadline 10 — both fit (1 + 1 = 2 ≤ 10).
        check(s.scheduleCourse(new int[][]{{1, 10}, {1, 10}}), 2, "two shorts same deadline");

        // The classic "swap" case: a long course followed by a short one.
        // courses = [[5,5],[4,6],[2,6]]:
        //   day 0: take (5,5) → time=5 ≤ 5, kept = {5}
        //   day 1: try (4,6) → time=9 > 6, evict max (5)  → time=4, kept = {4}
        //   day 2: try (2,6) → time=6 ≤ 6, kept = {4, 2}
        // Answer: 2.
        check(s.scheduleCourse(new int[][]{{5, 5}, {4, 6}, {2, 6}}), 2, "swap shorter for longer");

        // All courses fit.
        check(s.scheduleCourse(new int[][]{{1, 2}, {2, 4}, {3, 7}, {1, 8}}), 4, "everything fits");

        // None fit (all durations exceed deadlines).
        check(s.scheduleCourse(new int[][]{{10, 5}, {20, 10}}), 0, "nothing fits");

        // Empty / null
        check(s.scheduleCourse(new int[][]{}), 0, "empty");
        check(s.scheduleCourse(null), 0, "null");
    }

    private static void check(int got, int expected, String label) {
        boolean ok = got == expected;
        System.out.println(label + ": " + got + (ok ? "  OK" : "  FAIL (expected " + expected + ")"));
    }
}
