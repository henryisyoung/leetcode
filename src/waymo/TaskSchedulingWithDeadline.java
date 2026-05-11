package waymo;
/*
Task Scheduling with Deadline

Given a set of n tasks, each with:
  - id            (unique integer)
  - duration      (hours, sequential — see "scheduling model" below)
  - prerequisites (other task ids that must finish before this one starts)

And a global deadline.  Return an order in which to run the tasks so:
  (a) every prerequisite of a task appears earlier in the order, and
  (b) the cumulative duration ≤ deadline.

If no such order exists, return -1 (or null in code).

Note on the spec's example
  The spec's example shows tasks with total duration 3+2+1+2+4 = 12 and
  deadline 10, but lists [1, 3, 2, 4, 5] as the "Example output".  The
  example output appears to demonstrate the *format* of a valid order,
  not the answer for this specific input — under a single-machine
  sequential model, 12 > 10 so the correct answer is -1.  The
  implementation below returns -1 for that input.  With deadline=12 (or
  larger), it returns a valid topological order such as [1, 2, 3, 4, 5].

Constraints
  1 <= n        <= 100
  1 <= duration <= 100
  1 <= deadline <= 100
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/*
Scheduling model: single machine, sequential execution.
  Total time = sum(durations).  Order doesn't change the total, only the
  ordering constraint matters for feasibility.  So:
      feasible  ⇔  graph is acyclic AND sum(durations) ≤ deadline

  (If the problem instead wanted "unlimited workers, finish all by deadline",
   the feasibility check becomes critical-path length ≤ deadline.  That's a
   one-line swap of `totalDuration` for `criticalPathLength`; see the
   `criticalPathLength` helper below.)

Algorithm: Kahn's topological sort with a min-heap tiebreak.

  1. Build the dependency graph.  In-degree of each task = #prerequisites.
  2. Push every zero-indegree task onto a min-heap keyed by id.
  3. Pop the smallest-id ready task, append to the output order, then
     decrement the indegree of every dependent.  When a dependent hits
     zero, push it.
  4. If the produced order is shorter than n, the graph has a cycle → -1.
  5. Otherwise check sum(durations) ≤ deadline.  If not, -1.

Why min-heap by id (and not, say, by duration):
  - The problem doesn't define "optimal" precisely — *any* topological
    order with the right total length is a valid answer.
  - Smallest-id-first gives a deterministic, easy-to-read output that's
    stable across machines.  It also matches the most common reference
    answer for "Kahn's with deterministic tiebreak".

Complexity
  Time:   O((V + E) log V)  — Kahn's with a heap.  At n ≤ 100 this is microseconds.
  Memory: O(V + E)          — adjacency + indegree maps.
*/
public class TaskSchedulingWithDeadline {

    /** Minimal task model.  Prereqs are stored as ids (not Task refs) so input parsing stays trivial. */
    public static final class Task {
        public final int id;
        public final int duration;
        public final List<Integer> prerequisites;

        public Task(int id, int duration, List<Integer> prerequisites) {
            this.id = id;
            this.duration = duration;
            this.prerequisites = prerequisites == null
                    ? Collections.emptyList()
                    : Collections.unmodifiableList(prerequisites);
        }

        @Override
        public String toString() {
            return "{id=" + id + " dur=" + duration + " prereq=" + prerequisites + "}";
        }
    }

    /** Returns a feasible order of task ids, or {@code null} if no such order exists. */
    public List<Integer> schedule(List<Task> tasks, int deadline) {
        if (tasks == null) return null;
        int n = tasks.size();
        if (n == 0) return Collections.emptyList();

        // Lookup + cumulative cost.
        Map<Integer, Task> byId = new HashMap<>();
        long total = 0;
        for (Task t : tasks) {
            if (byId.containsKey(t.id)) return null;          // duplicate id
            byId.put(t.id, t);
            total += t.duration;
        }
        if (total > deadline) return null;                    // unreachable under sequential model

        // Build indegree + dependents map.  Validate that every referenced prereq exists.
        Map<Integer, Integer> inDeg = new HashMap<>();
        Map<Integer, List<Integer>> dependents = new HashMap<>();
        for (Task t : tasks) {
            inDeg.put(t.id, t.prerequisites.size());
            dependents.put(t.id, new ArrayList<>());
        }
        for (Task t : tasks) {
            for (int p : t.prerequisites) {
                if (!byId.containsKey(p)) return null;        // bad prereq reference
                dependents.get(p).add(t.id);
            }
        }

        // Kahn's algorithm with a min-heap on id for deterministic output.
        PriorityQueue<Integer> ready = new PriorityQueue<>();
        for (Task t : tasks) if (inDeg.get(t.id) == 0) ready.offer(t.id);

        List<Integer> order = new ArrayList<>(n);
        while (!ready.isEmpty()) {
            int u = ready.poll();
            order.add(u);
            for (int v : dependents.get(u)) {
                int d = inDeg.get(v) - 1;
                inDeg.put(v, d);
                if (d == 0) ready.offer(v);
            }
        }

        // If we couldn't drain everyone, there was a cycle.
        return order.size() == n ? order : null;
    }

    /* --------------------------- Helpers --------------------------- */

    /**
     * Critical-path length (longest dependency chain weighted by duration).
     * The minimum makespan if you had unlimited workers.  Returns -1 on cycles.
     *
     * Provided as a reference for "what if the model were parallel?":
     *   feasible_parallel  ⇔  criticalPathLength(tasks) ≤ deadline
     */
    public long criticalPathLength(List<Task> tasks) {
        Map<Integer, Task> byId = new HashMap<>();
        for (Task t : tasks) byId.put(t.id, t);

        Map<Integer, Long> finish = new HashMap<>();
        // Topo-order traversal — reuse the schedule logic for the order, ignore deadline.
        List<Integer> order = topoOrderIgnoreDeadline(tasks);
        if (order == null) return -1;

        long maxFinish = 0;
        for (int id : order) {
            Task t = byId.get(id);
            long start = 0;
            for (int p : t.prerequisites) start = Math.max(start, finish.get(p));
            long f = start + t.duration;
            finish.put(id, f);
            maxFinish = Math.max(maxFinish, f);
        }
        return maxFinish;
    }

    private List<Integer> topoOrderIgnoreDeadline(List<Task> tasks) {
        Map<Integer, Integer> inDeg = new HashMap<>();
        Map<Integer, List<Integer>> dependents = new HashMap<>();
        for (Task t : tasks) {
            inDeg.put(t.id, t.prerequisites.size());
            dependents.put(t.id, new ArrayList<>());
        }
        for (Task t : tasks) for (int p : t.prerequisites) dependents.get(p).add(t.id);
        PriorityQueue<Integer> ready = new PriorityQueue<>();
        for (Task t : tasks) if (inDeg.get(t.id) == 0) ready.offer(t.id);
        List<Integer> order = new ArrayList<>();
        while (!ready.isEmpty()) {
            int u = ready.poll();
            order.add(u);
            for (int v : dependents.get(u)) {
                int d = inDeg.get(v) - 1;
                inDeg.put(v, d);
                if (d == 0) ready.offer(v);
            }
        }
        return order.size() == tasks.size() ? order : null;
    }

    /** Validate that {@code order} is a topological sort of {@code tasks}.  Used by tests. */
    public static boolean isValidTopoOrder(List<Integer> order, List<Task> tasks) {
        if (order.size() != tasks.size()) return false;
        Map<Integer, Integer> pos = new HashMap<>();
        for (int i = 0; i < order.size(); i++) pos.put(order.get(i), i);
        if (pos.size() != order.size()) return false;       // duplicates
        Map<Integer, Task> byId = new HashMap<>();
        for (Task t : tasks) byId.put(t.id, t);
        for (Task t : tasks) {
            Integer myIdx = pos.get(t.id);
            if (myIdx == null) return false;
            for (int p : t.prerequisites) {
                Integer pIdx = pos.get(p);
                if (pIdx == null || pIdx >= myIdx) return false;
            }
        }
        return true;
    }

    /* --------------------------- IO --------------------------- */

    public static void main(String[] args) {
        runDemos();
    }

    private static void runDemos() {
        TaskSchedulingWithDeadline solver = new TaskSchedulingWithDeadline();

        // ---------- Spec example (deadline=10 fails because total=12 > 10) ----------
        List<Task> specTasks = Arrays.asList(
                new Task(1, 3, Collections.emptyList()),
                new Task(2, 2, Arrays.asList(1)),
                new Task(3, 1, Collections.emptyList()),
                new Task(4, 2, Arrays.asList(2, 3)),
                new Task(5, 4, Arrays.asList(4))
        );
        checkInfeasible(solver, "spec example, deadline=10 (total=12, infeasible)", specTasks, 10);

        // Same tasks with a larger deadline succeed.
        checkFeasible(solver, "spec example, deadline=12 (total=12, just fits)", specTasks, 12);
        checkFeasible(solver, "spec example, deadline=100 (slack)",              specTasks, 100);

        // ---------- Trivial cases ----------
        checkFeasible(solver, "empty task list",   Collections.emptyList(), 5);

        checkFeasible(solver, "single task, fits", Arrays.asList(
                new Task(7, 3, Collections.emptyList())
        ), 3);
        checkInfeasible(solver, "single task, doesn't fit", Arrays.asList(
                new Task(7, 5, Collections.emptyList())
        ), 4);

        // ---------- Linear chain forces a unique order ----------
        List<Task> chain = Arrays.asList(
                new Task(1, 1, Collections.emptyList()),
                new Task(2, 1, Arrays.asList(1)),
                new Task(3, 1, Arrays.asList(2)),
                new Task(4, 1, Arrays.asList(3)),
                new Task(5, 1, Arrays.asList(4))
        );
        checkOrder(solver, "linear 1→2→3→4→5", chain, 5, Arrays.asList(1, 2, 3, 4, 5));

        // ---------- No prereqs — min-heap returns ids in ascending order ----------
        List<Task> indie = Arrays.asList(
                new Task(3, 1, Collections.emptyList()),
                new Task(1, 1, Collections.emptyList()),
                new Task(2, 1, Collections.emptyList())
        );
        checkOrder(solver, "no prereqs (min-heap → sorted by id)", indie, 3, Arrays.asList(1, 2, 3));

        // ---------- Cyclic dependency → infeasible ----------
        List<Task> cyc = Arrays.asList(
                new Task(1, 1, Arrays.asList(2)),
                new Task(2, 1, Arrays.asList(1))
        );
        checkInfeasible(solver, "cycle 1↔2", cyc, 100);

        // ---------- Self-loop → infeasible ----------
        checkInfeasible(solver, "self-loop", Arrays.asList(
                new Task(1, 1, Arrays.asList(1))
        ), 100);

        // ---------- Bad prereq reference → infeasible ----------
        checkInfeasible(solver, "prereq references missing task", Arrays.asList(
                new Task(1, 1, Arrays.asList(99))
        ), 100);

        // ---------- Two disconnected DAGs ----------
        List<Task> twoDags = Arrays.asList(
                new Task(1, 1, Collections.emptyList()),
                new Task(2, 1, Arrays.asList(1)),
                new Task(10, 1, Collections.emptyList()),
                new Task(11, 1, Arrays.asList(10))
        );
        checkFeasible(solver, "two disconnected DAGs", twoDags, 4);

        // ---------- Critical-path helper (for the "parallel workers" interpretation) ----------
        long cp = solver.criticalPathLength(specTasks);
        System.out.printf("Critical path length for spec example = %d  (sequential total = %d)%n",
                cp, 12);

        // ---------- Stress: n = 100, dense DAG ----------
        List<Task> big = buildLayeredDag(/*layers*/ 10, /*perLayer*/ 10, /*durEach*/ 1);
        long t0 = System.nanoTime();
        List<Integer> bigOrder = solver.schedule(big, 100);
        long us = (System.nanoTime() - t0) / 1_000;
        boolean ok = bigOrder != null && isValidTopoOrder(bigOrder, big);
        System.out.printf("%s 100-task layered DAG: %d µs, order length %d%n",
                ok ? "OK   " : "FAIL ", us, bigOrder == null ? -1 : bigOrder.size());
    }

    /* --------------------------- Test plumbing --------------------------- */

    private static void checkFeasible(TaskSchedulingWithDeadline solver, String name,
                                      List<Task> tasks, int deadline) {
        List<Integer> order = solver.schedule(tasks, deadline);
        boolean ok = order != null && isValidTopoOrder(order, tasks);
        long total = 0;
        for (Task t : tasks) total += t.duration;
        if (total > deadline) ok = false;
        System.out.printf("%s %s → %s%n", ok ? "OK   " : "FAIL ", name, order);
    }

    private static void checkInfeasible(TaskSchedulingWithDeadline solver, String name,
                                        List<Task> tasks, int deadline) {
        List<Integer> order = solver.schedule(tasks, deadline);
        boolean ok = order == null;
        System.out.printf("%s %s → %s%n", ok ? "OK   " : "FAIL ", name,
                order == null ? "-1" : order);
    }

    private static void checkOrder(TaskSchedulingWithDeadline solver, String name,
                                   List<Task> tasks, int deadline, List<Integer> expected) {
        List<Integer> order = solver.schedule(tasks, deadline);
        boolean ok = order != null && order.equals(expected);
        System.out.printf("%s %s → %s%s%n", ok ? "OK   " : "FAIL ", name, order,
                ok ? "" : " (expected " + expected + ")");
    }

    /** Build a layered DAG: each task in layer L depends on every task in layer L-1. */
    private static List<Task> buildLayeredDag(int layers, int perLayer, int durEach) {
        List<Task> out = new ArrayList<>();
        int id = 1;
        List<Integer> prev = Collections.emptyList();
        for (int L = 0; L < layers; L++) {
            List<Integer> cur = new ArrayList<>();
            for (int i = 0; i < perLayer; i++) {
                out.add(new Task(id, durEach, prev));
                cur.add(id);
                id++;
            }
            prev = cur;
        }
        return out;
    }

    /* --------------------------- Unused but-documented: DFS-style topo --------------------------- */

    /**
     * Alternate implementation: DFS-based topo sort.  Same answer when the
     * graph is acyclic; uses a colour map to detect cycles.  Kept here as a
     * reference because some interviewers prefer it over Kahn's.
     */
    @SuppressWarnings("unused")
    List<Integer> scheduleDfs(List<Task> tasks, int deadline) {
        int n = tasks.size();
        Map<Integer, Task> byId = new HashMap<>();
        long total = 0;
        for (Task t : tasks) { byId.put(t.id, t); total += t.duration; }
        if (total > deadline) return null;

        Map<Integer, List<Integer>> dependents = new HashMap<>();
        for (Task t : tasks) dependents.put(t.id, new ArrayList<>());
        for (Task t : tasks) for (int p : t.prerequisites) {
            if (!byId.containsKey(p)) return null;
            dependents.get(p).add(t.id);
        }

        // 0 = unvisited, 1 = visiting (on current DFS stack), 2 = done.
        Map<Integer, Integer> colour = new HashMap<>();
        for (Task t : tasks) colour.put(t.id, 0);
        Deque<Integer> stack = new java.util.ArrayDeque<>();
        Deque<Integer> iter = new java.util.ArrayDeque<>();
        List<Integer> rev = new ArrayList<>();
        List<Integer> roots = new ArrayList<>();
        for (Task t : tasks) if (t.prerequisites.isEmpty()) roots.add(t.id);
        Collections.sort(roots);
        for (int root : roots) {
            if (colour.get(root) != 0) continue;
            stack.push(root); iter.push(0); colour.put(root, 1);
            while (!stack.isEmpty()) {
                int u = stack.peek();
                int it = iter.peek();
                List<Integer> deps = dependents.get(u);
                if (it >= deps.size()) {
                    rev.add(u);
                    colour.put(u, 2);
                    stack.pop(); iter.pop();
                    continue;
                }
                iter.pop(); iter.push(it + 1);
                int v = deps.get(it);
                int cv = colour.get(v);
                if (cv == 1) return null;            // back-edge → cycle
                if (cv == 0) { stack.push(v); iter.push(0); colour.put(v, 1); }
            }
        }
        if (rev.size() != n) return null;             // disconnected unvisited nodes ⇒ shouldn't happen here
        Collections.reverse(rev);
        return rev;
    }

    /* --------------------------- (Helpers for the IO format are intentionally omitted —
        the spec's JSON-in-stdin form is hand-edited rather than piped, so the demo
        constructs tasks directly.  If stdin parsing is needed, plug in any JSON
        library or hand-roll a tiny parser at the call site.) --------------------------- */
}
