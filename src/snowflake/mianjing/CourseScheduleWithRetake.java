package snowflake.mianjing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
OOD variant of ParallelCourses (LeetCode 1136 / 2050).
https://www.1point3acres.com/bbs/thread-1166355-1-1.html
Setup
-----
Each Course is an object that knows only its own id and its own prerequisites
(`prevCourses`). The student is given a list of "root" courses they want to
finish — these are the *target* / leaf-of-the-goal-tree courses. The full
dependency graph is not provided; we have to discover it ourselves by walking
back through `prevCourses` (BFS).

We are also given an external API `CourseChecker.isFailed(course)` that the
student can call after attempting a course to learn whether they passed. If a
course fails it must be retaken before any of its dependents can be unlocked,
so we just throw it back onto the queue and try again later.

Algorithm
---------
1. BFS from the roots through `prevCourses` to:
     - discover every course in the dependency graph,
     - build the reverse adjacency map (course -> dependents that need it),
     - record each course's in-degree (number of un-passed prerequisites).

2. Topological walk:
     - Seed the queue with every course that has zero prerequisites.
     - Pop a course, attempt it (call the API).
         * If `isFailed`, push it back — we'll retake it later. Its dependents
           stay locked.
         * Otherwise, record completion and decrement the in-degree of each
           dependent; if a dependent hits zero, it becomes available.
     - Continue until the queue is empty.

Notes
-----
- Retakes don't change the topological structure, only how many times each
  course is attempted. The graph still has to be a DAG; if it isn't, we can't
  finish, and we report that.
- If `isFailed` is allowed to fail the same course forever, the loop wouldn't
  terminate; in a real system you'd cap retries. A `maxRetries` knob is left
  here for safety.
 */
public class CourseScheduleWithRetake {

    /** A course node carries only its own id and its own prerequisites. */
    public static class Course {
        public final int id;
        public final List<Course> prevCourses;

        public Course(int id) {
            this(id, new ArrayList<>());
        }

        public Course(int id, List<Course> prevCourses) {
            this.id = id;
            this.prevCourses = prevCourses != null ? prevCourses : new ArrayList<>();
        }

        @Override
        public String toString() {
            return "Course(" + id + ")";
        }
    }

    /** External API: tells us whether the most recent attempt at `course` failed. */
    public interface CourseChecker {
        boolean isFailed(Course course);
    }

    /**
     * Attempt every course reachable from `roots` (via prevCourses), retaking any
     * course the checker reports as failed.
     *
     * @return the order in which courses were finally passed.
     * @throws IllegalStateException if the dependency graph has a cycle.
     */
    public List<Integer> takeCourses(List<Course> roots, CourseChecker checker) {
        return takeCourses(roots, checker, Integer.MAX_VALUE);
    }

    /**
     * Same as above, but caps retakes per course so a misbehaving checker can't
     * loop forever.
     */
    public List<Integer> takeCourses(List<Course> roots, CourseChecker checker, int maxRetries) {
        if (roots == null || roots.isEmpty()) return new ArrayList<>();

        // Step 1: BFS through prevCourses to discover the full graph.
        Map<Course, Integer> inDegree = new HashMap<>();
        Map<Course, List<Course>> dependents = new HashMap<>();
        Set<Course> seen = new HashSet<>();

        Deque<Course> bfs = new ArrayDeque<>();
        for (Course root : roots) {
            if (root != null && seen.add(root)) bfs.offer(root);
        }
        while (!bfs.isEmpty()) {
            Course cur = bfs.poll();
            inDegree.putIfAbsent(cur, cur.prevCourses.size());
            dependents.putIfAbsent(cur, new ArrayList<>());
            for (Course prev : cur.prevCourses) {
                dependents.computeIfAbsent(prev, k -> new ArrayList<>()).add(cur);
                if (seen.add(prev)) bfs.offer(prev);
            }
        }

        // Step 2: topological walk with a retake-on-fail check at pop time.
        Deque<Course> queue = new ArrayDeque<>();
        for (Map.Entry<Course, Integer> e : inDegree.entrySet()) {
            if (e.getValue() == 0) queue.offer(e.getKey());
        }

        Map<Course, Integer> attempts = new HashMap<>();
        List<Integer> passedOrder = new ArrayList<>();

        while (!queue.isEmpty()) {
            Course cur = queue.poll();
            int tries = attempts.merge(cur, 1, Integer::sum);

            if (checker.isFailed(cur)) {
                if (tries >= maxRetries) {
                    throw new IllegalStateException(
                            "Course " + cur.id + " kept failing after " + tries + " attempts");
                }
                queue.offer(cur);
                continue;
            }

            passedOrder.add(cur.id);
            for (Course next : dependents.getOrDefault(cur, Collections.emptyList())) {
                int remaining = inDegree.merge(next, -1, Integer::sum);
                if (remaining == 0) queue.offer(next);
            }
        }

        if (passedOrder.size() != seen.size()) {
            throw new IllegalStateException("Dependency graph has a cycle; cannot complete all courses.");
        }
        return passedOrder;
    }

    /**
     * Parallel-courses style answer: how many semesters do we need if every
     * available (and not-currently-failed) course can be taken in parallel
     * each semester? Each `isFailed` call represents that semester's attempt.
     */
    public int minimumSemesters(List<Course> roots, CourseChecker checker) {
        if (roots == null || roots.isEmpty()) return 0;

        Map<Course, Integer> inDegree = new HashMap<>();
        Map<Course, List<Course>> dependents = new HashMap<>();
        Set<Course> seen = new HashSet<>();

        Deque<Course> bfs = new ArrayDeque<>();
        for (Course root : roots) {
            if (root != null && seen.add(root)) bfs.offer(root);
        }
        while (!bfs.isEmpty()) {
            Course cur = bfs.poll();
            inDegree.putIfAbsent(cur, cur.prevCourses.size());
            dependents.putIfAbsent(cur, new ArrayList<>());
            for (Course prev : cur.prevCourses) {
                dependents.computeIfAbsent(prev, k -> new ArrayList<>()).add(cur);
                if (seen.add(prev)) bfs.offer(prev);
            }
        }

        Deque<Course> ready = new ArrayDeque<>();
        for (Map.Entry<Course, Integer> e : inDegree.entrySet()) {
            if (e.getValue() == 0) ready.offer(e.getKey());
        }

        int semesters = 0, passed = 0;
        while (!ready.isEmpty()) {
            semesters++;
            int size = ready.size();
            List<Course> retakes = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                Course cur = ready.poll();
                if (checker.isFailed(cur)) {
                    retakes.add(cur);
                    continue;
                }
                passed++;
                for (Course next : dependents.getOrDefault(cur, Collections.emptyList())) {
                    int remaining = inDegree.merge(next, -1, Integer::sum);
                    if (remaining == 0) ready.offer(next);
                }
            }
            ready.addAll(retakes);
        }

        if (passed != seen.size()) {
            throw new IllegalStateException("Dependency graph has a cycle; cannot complete all courses.");
        }
        return semesters;
    }

    // ---------------------------------------------------------------------
    // Demo / sanity check
    // ---------------------------------------------------------------------
    public static void main(String[] args) {
        // Graph:
        //   1 ── ┐
        //         └── 3 ── ┐
        //   2 ── ┘          └── 5
        //              4 ── ┘
        Course c1 = new Course(1);
        Course c2 = new Course(2);
        Course c3 = new Course(3, List.of(c1, c2));
        Course c4 = new Course(4);
        Course c5 = new Course(5, List.of(c3, c4));

        CourseScheduleWithRetake s = new CourseScheduleWithRetake();

        // No failures: every course passes on first try.
        CourseChecker neverFails = course -> false;
        System.out.println(s.takeCourses(List.of(c5), neverFails));
        // e.g. [1, 2, 4, 3, 5] (any valid topo order)

        System.out.println(s.minimumSemesters(List.of(c5), neverFails));
        // 3   (sem1: {1,2,4}, sem2: {3}, sem3: {5})

        // Course 3 fails twice, then passes. Course 5 still has to wait for it.
        CourseChecker flaky = new CourseChecker() {
            final Map<Integer, Integer> remainingFailures = new HashMap<>(Map.of(3, 2));
            @Override
            public boolean isFailed(Course course) {
                int left = remainingFailures.getOrDefault(course.id, 0);
                if (left > 0) {
                    remainingFailures.put(course.id, left - 1);
                    return true;
                }
                return false;
            }
        };
        System.out.println(s.takeCourses(List.of(c5), flaky));
        // 5 still appears last; 3 is attempted three times before passing.

        // Per-semester variant with the same flaky checker (fresh state).
        CourseChecker flaky2 = new CourseChecker() {
            final Map<Integer, Integer> remainingFailures = new HashMap<>(Map.of(3, 2));
            @Override
            public boolean isFailed(Course course) {
                int left = remainingFailures.getOrDefault(course.id, 0);
                if (left > 0) {
                    remainingFailures.put(course.id, left - 1);
                    return true;
                }
                return false;
            }
        };
        System.out.println(s.minimumSemesters(List.of(c5), flaky2));
        // 5 (sem1: {1,2,4}, sem2-4: 3 fails twice then passes, sem5: 5)
    }
}
