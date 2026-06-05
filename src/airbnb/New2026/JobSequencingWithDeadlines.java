package airbnb.New2026;
/*
Job Sequencing with Deadlines  (maximise total reward).

Each task has (id, deadline, reward). One task per time unit; a task
finished at time t is valid iff t <= deadline. Select a subset of
tasks AND an order so that every chosen task meets its deadline and
the total reward is maximised. Return the order in which to run them.

I/O
  Input : List<Task>  (id, deadline >= 1, reward >= 0)
  Output: List<String> task ids in execution order

Constraints
  1 <= n <= 1000
  1 <= deadline <= n  (without loss of generality — deadlines beyond
                       the number of tasks add no value because we
                       can complete at most n tasks)
  0 <= reward

Example
  [('a',2,8), ('b',1,3), ('c',2,5), ('d',3,3)]
  Optimal value = 8 + 5 + 3 = 16
  Order:        [c, a, d]  or  [a, c, d]   (b is dropped)
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/*
Algorithm — sort + min-heap (classic O(n log n)).

  The "avoid the naive greedy" hint in the problem is steering you
  away from "sort by reward desc, scan an n-slot calendar from the
  deadline down to 1 looking for a free slot", which is O(n^2). The
  improved solution uses a min-heap to make the dominance check O(1)
  amortised.

  Idea
    Sort tasks by DEADLINE ascending. Maintain a min-heap of currently
    SELECTED tasks keyed on reward. Walk through:
        - if heap.size() < task.deadline: we still have a free time
          slot before this deadline, so simply add the task.
        - else if heap.peek().reward < task.reward: the cheapest
          already-selected task can be replaced by this richer one
          without violating any deadline (because the heap size
          stays the same and every task in the heap has a deadline
          <= current task's deadline).
        - else: discard the task.

  Why correctness holds
    At every moment, |heap| == number of tasks scheduled so far, all
    with deadlines <= current task's deadline. Whenever we pop the
    minimum and push a larger one, total reward strictly increases
    and feasibility is preserved (we filled the same number of slots
    by their earliest deadline). This is the standard exchange
    argument; the algorithm is optimal.

  Output order
    After processing, the heap holds the chosen tasks. To run them
    feasibly, sort them by deadline ascending — any feasible
    permutation is acceptable per the problem. (Ties may be broken
    arbitrarily; the problem says either [c,a,d] or [a,c,d] is fine.)

  Edge case: deadline > n
    Schedules with more than n slots are wasted (only n tasks exist).
    Clamping the deadline to n keeps the heap argument tight without
    affecting correctness. We don't clamp explicitly — the heap-size
    comparison `heap.size() < deadline` naturally caps things.

Complexity
  Time:   O(n log n)   — one sort + n heap ops
  Memory: O(n)
*/
public class JobSequencingWithDeadlines {

    public static final class Task {
        public final String id;
        public final int deadline;
        public final long reward;
        public Task(String id, int deadline, long reward) {
            this.id = id; this.deadline = deadline; this.reward = reward;
        }
        @Override public String toString() {
            return "(" + id + "," + deadline + "," + reward + ")";
        }
    }

    public static final class Result {
        public final List<String> order;
        public final long totalReward;
        public Result(List<String> order, long totalReward) {
            this.order = order; this.totalReward = totalReward;
        }
    }

    public Result schedule(List<Task> tasks) {
        // Defensive copy so we don't mutate the caller's list.
        List<Task> sorted = new ArrayList<>(tasks);
        sorted.sort(Comparator.comparingInt(a -> a.deadline));

        PriorityQueue<Task> pq = new PriorityQueue<>((a, b) -> {
           if (a.reward != b.reward) return Long.compare(a.reward, b.reward);
           return a.deadline - b.deadline;
        });

        for (Task task : sorted) {
            int deadline = task.deadline;
            if (pq.size() < deadline) {
                pq.add(task);
            } else if (!pq.isEmpty() && pq.peek().reward < task.reward) {
                pq.poll();              // only swap when the new task is strictly richer
                pq.add(task);
            }
        }

        // Execution order must meet deadlines: sort chosen tasks by deadline
        // ascending (id breaks ties for determinism). Heap order is by reward
        // and would not be feasible.
        List<Task> chosen = new ArrayList<>(pq);
        chosen.sort((a, b) -> {
            if (a.deadline != b.deadline) return a.deadline - b.deadline;
            return a.id.compareTo(b.id);
        });

        List<String> order = new ArrayList<>();
        long total = 0;
        for (Task task : chosen) {
            total += task.reward;
            order.add(task.id);
        }
        return new Result(order, total);
    }

    /* --------------------------- IO + demo --------------------------- */

    public static void main(String[] args) throws IOException {
        if (args.length == 0 && hasStdin()) {
            runFromStdin();
            return;
        }
        runDemos();
    }

    private static boolean hasStdin() {
        try { return System.in.available() > 0; } catch (IOException e) { return false; }
    }

    /**
     * Stdin format:
     *   line 1: N
     *   next N lines: "id deadline reward"
     */
    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine().trim());
        List<Task> tasks = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            String[] parts = br.readLine().trim().split("\\s+");
            tasks.add(new Task(parts[0], Integer.parseInt(parts[1]), Long.parseLong(parts[2])));
        }
        Result r = new JobSequencingWithDeadlines().schedule(tasks);
        System.out.println("reward=" + r.totalReward);
        System.out.println("order =" + r.order);
    }

    private static void runDemos() {
        JobSequencingWithDeadlines solver = new JobSequencingWithDeadlines();

        // ---- Spec example ----
        Result r1 = solver.schedule(Arrays.asList(
                new Task("a", 2, 8),
                new Task("b", 1, 3),
                new Task("c", 2, 5),
                new Task("d", 3, 3)));
        check("ex1 total reward", r1.totalReward, 16L);
        check("ex1 chosen = {a, c, d}", new HashSet<>(r1.order),
                new HashSet<>(Arrays.asList("a", "c", "d")));
        check("ex1 feasible order", isFeasible(r1.order, exTasks1()), true);
        check("ex1 matches one of [c,a,d] / [a,c,d]",
                r1.order.equals(Arrays.asList("c", "a", "d")) ||
                r1.order.equals(Arrays.asList("a", "c", "d")), true);

        // ---- Classic GfG example ----
        // (a,2,100), (b,1,19), (c,2,27), (d,1,25), (e,3,15)
        // optimal: {a, c, e} or {a, c, d}? Actually with deadlines:
        //  a=2,100   c=2,27  -> can both fit slots 1 and 2 -> 127
        //  +e=3,15   -> 142
        Result r2 = solver.schedule(Arrays.asList(
                new Task("a", 2, 100),
                new Task("b", 1, 19),
                new Task("c", 2, 27),
                new Task("d", 1, 25),
                new Task("e", 3, 15)));
        check("gfg total reward", r2.totalReward, 142L);
        check("gfg chosen = {a,c,e}", new HashSet<>(r2.order),
                new HashSet<>(Arrays.asList("a", "c", "e")));

        // ---- All-same deadline (pick the top-k by reward) ----
        Result r3 = solver.schedule(Arrays.asList(
                new Task("p", 2, 1),
                new Task("q", 2, 5),
                new Task("r", 2, 3),
                new Task("s", 2, 10)));
        check("all-d2 total reward", r3.totalReward, 15L);  // 10 + 5
        check("all-d2 size 2", r3.order.size(), 2);

        // ---- Deadlines beyond n are harmless ----
        Result r4 = solver.schedule(Arrays.asList(
                new Task("x", 100, 7),
                new Task("y", 100, 9),
                new Task("z", 100, 5)));
        check("huge deadlines all fit", r4.totalReward, 21L);

        // ---- Single task / empty ----
        check("single task", solver.schedule(Arrays.asList(new Task("only", 1, 42))).totalReward, 42L);
        check("empty input",  solver.schedule(new ArrayList<>()).order.size(), 0);
        check("zero reward task picked",
                solver.schedule(Arrays.asList(new Task("z", 1, 0))).order, Arrays.asList("z"));

        // ---- Cross-check vs brute force on small inputs ----
        java.util.Random rnd = new java.util.Random(42);
        int trials = 50;
        int ok = 0;
        for (int trial = 0; trial < trials; trial++) {
            int n = 1 + rnd.nextInt(6);                       // up to 6 tasks
            List<Task> ts = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                ts.add(new Task("t" + i, 1 + rnd.nextInt(n), rnd.nextInt(100)));
            }
            long heap = solver.schedule(ts).totalReward;
            long brute = bruteForceMaxReward(ts);
            if (heap == brute) ok++;
            else {
                System.out.println("MISMATCH " + ts + " heap=" + heap + " brute=" + brute);
            }
        }
        check("brute-force cross-check", ok, trials);

        // ---- Stress: n = 1000 ----
        int N = 1000;
        List<Task> big = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            big.add(new Task("t" + i, 1 + rnd.nextInt(N), rnd.nextInt(1_000_000)));
        }
        long t0 = System.nanoTime();
        Result rb = solver.schedule(big);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        check("stress feasible", isFeasible(rb.order, big), true);
        System.out.println("Stress n=" + N + ": chose=" + rb.order.size()
                + " reward=" + rb.totalReward + " in " + ms + " ms");
    }

    /* --------------------------- helpers --------------------------- */

    private static List<Task> exTasks1() {
        return Arrays.asList(
                new Task("a", 2, 8),
                new Task("b", 1, 3),
                new Task("c", 2, 5),
                new Task("d", 3, 3));
    }

    /** Is `order` a feasible schedule given `original` tasks? */
    private static boolean isFeasible(List<String> order, List<Task> original) {
        java.util.Map<String, Task> byId = new java.util.HashMap<>();
        for (Task t : original) byId.put(t.id, t);
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < order.size(); i++) {
            Task t = byId.get(order.get(i));
            if (t == null) return false;
            if (!seen.add(t.id)) return false;
            if (i + 1 > t.deadline) return false;             // finishes at time i+1
        }
        return true;
    }

    /** Brute-force: try every subset, every permutation. O(n! * n) — n <= 6 only. */
    private static long bruteForceMaxReward(List<Task> tasks) {
        int n = tasks.size();
        long best = 0;
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) perm[i] = i;
        long bestRef = best;
        long[] holder = {bestRef};
        permute(tasks, perm, 0, holder);
        return holder[0];
    }

    private static void permute(List<Task> tasks, int[] p, int i, long[] best) {
        if (i == p.length) {
            // Try all prefixes of this permutation as our schedule.
            long total = 0;
            for (int k = 0; k < p.length; k++) {
                Task t = tasks.get(p[k]);
                if (k + 1 > t.deadline) {
                    // This task at slot k+1 misses deadline; skip it for THIS prefix
                    // by considering the schedule that stops just before it.
                    if (total > best[0]) best[0] = total;
                    return;
                }
                total += t.reward;
                if (total > best[0]) best[0] = total;
            }
            return;
        }
        for (int j = i; j < p.length; j++) {
            int tmp = p[i]; p[i] = p[j]; p[j] = tmp;
            permute(tasks, p, i + 1, best);
            tmp = p[i]; p[i] = p[j]; p[j] = tmp;
        }
    }

    private static void check(String label, long got, long expected) {
        boolean ok = got == expected;
        System.out.println((ok ? "OK   " : "FAIL ") + label + "  got=" + got + " expected=" + expected);
    }
    private static void check(String label, int got, int expected) {
        boolean ok = got == expected;
        System.out.println((ok ? "OK   " : "FAIL ") + label + "  got=" + got + " expected=" + expected);
    }
    private static void check(String label, boolean got, boolean expected) {
        boolean ok = got == expected;
        System.out.println((ok ? "OK   " : "FAIL ") + label + "  got=" + got + " expected=" + expected);
    }
    private static void check(String label, Object got, Object expected) {
        boolean ok = got.equals(expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label + "  got=" + got + " expected=" + expected);
    }
}
