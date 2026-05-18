package airbnb.New2026;
/*
Two-Jug Water Problem — minimum operations + the operation sequence.

You have two jugs of capacities `j1` and `j2`. Initially both are empty.
At each step you may perform exactly one of:
  1. Fill jug k completely.
  2. Empty jug k.
  3. Pour from one jug to the other until the source is empty or the
     destination is full.

Return the minimum number of operations to reach a state where EITHER
jug contains exactly `target` units, and report the full sequence of
states (and the action taken to reach each).

I/O
  Input : int j1, int j2, int target
  Output: List<Step> — Step records (action, fromState, toState)

Constraints
  0 <= j1, j2 <= 1e4
  0 <= target <= 1e4
  Feasibility: target reachable iff
        target <= max(j1, j2)  AND  target % gcd(j1, j2) == 0
  (Bezout / number theory.) BFS confirms this directly, no need to
  pre-check unless we want a fast-fail.

Example
  j1=3, j2=5, target=4 -> 6 operations
  (0,0) -fill J2->     (0,5)
        -pour 2->1->   (3,2)
        -empty J1->    (0,2)
        -pour 2->1->   (2,0)
        -fill J2->     (2,5)
        -pour 2->1->   (3,4)   ✓ J2 holds 4
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/*
Algorithm — BFS on the (a, b) state space.

  Nodes:    pairs (a, b), 0 <= a <= j1, 0 <= b <= j2.
            At most (j1+1)*(j2+1) states.
  Edges:    six labelled transitions per state (most are unique;
            duplicates such as "fill J1 while already full" produce
            a self-loop and are pruned by the visited set).
  Goal:     any state with a == target OR b == target.
            (Start state (0,0) is itself the answer when target == 0.)
  Parent:   we store parent (prevState, action) in a HashMap keyed on
            the encoded state, then reconstruct the path at the end.

  State encoding: an int = a * (j2 + 1) + b. Fits in 32 bits easily
  (1e4 * 1e4 = 1e8 << 2^31).

  Why BFS and not DFS / number theory:
    Number theory tells us "reachable in some number of steps", not
    "in the FEWEST steps". BFS naturally yields minimum-op count and
    a witness sequence.

  Tight pre-check for infeasibility:
    g = gcd(j1, j2). If target > max(j1, j2) or target % g != 0
    (with g defined as 0 when both jugs are 0), bail out immediately.
    Saves O(j1*j2) BFS work on hopeless inputs.

Complexity
  Time:   O((j1 + 1) * (j2 + 1))   — each state explored once
  Memory: O((j1 + 1) * (j2 + 1))   — visited + parent map
*/
public class WaterJugMinOps {

    public enum Action {
        START,
        FILL_J1, FILL_J2,
        EMPTY_J1, EMPTY_J2,
        POUR_1_TO_2, POUR_2_TO_1;

        @Override public String toString() {
            switch (this) {
                case START:        return "start";
                case FILL_J1:      return "fill J1";
                case FILL_J2:      return "fill J2";
                case EMPTY_J1:     return "empty J1";
                case EMPTY_J2:     return "empty J2";
                case POUR_1_TO_2:  return "pour 1->2";
                case POUR_2_TO_1:  return "pour 2->1";
                default:           return name();
            }
        }
    }

    public static final class Step {
        public final Action action;
        public final int a, b;
        public Step(Action action, int a, int b) { this.action = action; this.a = a; this.b = b; }
        @Override public String toString() { return "(" + a + "," + b + ")  [" + action + "]"; }
    }

    /** Returns an empty list iff target is unreachable. The first step is always (0,0,start). */
    public List<Step> solve(int j1, int j2, int target) {
        if (j1 < 0 || j2 < 0 || target < 0) {
            throw new IllegalArgumentException("capacities and target must be >= 0");
        }
        if (target == 0) {
            return Collections.singletonList(new Step(Action.START, 0, 0));
        }
        if (target > Math.max(j1, j2)) return Collections.emptyList();
        int g = gcd(j1, j2);                                 // gcd(0,x)=x, gcd(0,0)=0
        if (g == 0 || target % g != 0) return Collections.emptyList();

        int W = j2 + 1;
        int start = 0;                                       // (0,0) -> 0
        Map<Integer, int[]> parent = new HashMap<>();        // encoded -> [parentEncoded, actionOrdinal]
        parent.put(start, new int[]{-1, Action.START.ordinal()});

        Deque<int[]> q = new ArrayDeque<>();
        q.offer(new int[]{0, 0});

        int goalA = -1, goalB = -1;
        outer:
        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int a = cur[0], b = cur[1];

            int[][] nexts = new int[][]{
                    {j1, b, Action.FILL_J1.ordinal()},
                    {a,  j2, Action.FILL_J2.ordinal()},
                    {0,  b,  Action.EMPTY_J1.ordinal()},
                    {a,  0,  Action.EMPTY_J2.ordinal()},
                    pour(a, b, j2, /*toSecond=*/true,  Action.POUR_1_TO_2.ordinal()),
                    pour(a, b, j1, /*toSecond=*/false, Action.POUR_2_TO_1.ordinal()),
            };

            for (int[] nx : nexts) {
                int na = nx[0], nb = nx[1], act = nx[2];
                if (na == a && nb == b) continue;             // self-loop (e.g. fill when full)
                int code = na * W + nb;
                if (parent.containsKey(code)) continue;
                parent.put(code, new int[]{a * W + b, act});
                if (na == target || nb == target) {
                    goalA = na; goalB = nb;
                    break outer;
                }
                q.offer(new int[]{na, nb});
            }
        }
        if (goalA < 0) return Collections.emptyList();
        return reconstruct(parent, goalA, goalB, W);
    }

    private static int[] pour(int a, int b, int capDst, boolean toSecond, int actOrdinal) {
        // toSecond=true : jug1 -> jug2 ; toSecond=false : jug2 -> jug1
        int src = toSecond ? a : b;
        int dst = toSecond ? b : a;
        int t = Math.min(src, capDst - dst);
        int na = toSecond ? a - t : a + t;
        int nb = toSecond ? b + t : b - t;
        return new int[]{na, nb, actOrdinal};
    }

    private static List<Step> reconstruct(Map<Integer, int[]> parent, int ga, int gb, int W) {
        List<Step> path = new ArrayList<>();
        int code = ga * W + gb;
        while (code != -1) {
            int a = code / W, b = code % W;
            int[] p = parent.get(code);
            path.add(new Step(Action.values()[p[1]], a, b));
            code = p[0];
        }
        Collections.reverse(path);
        return path;
    }

    private static int gcd(int x, int y) {
        x = Math.abs(x); y = Math.abs(y);
        while (y != 0) { int t = x % y; x = y; y = t; }
        return x;
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

    /** Stdin: one test per line: "j1 j2 target". Prints the sequence (one step per line). */
    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        WaterJugMinOps solver = new WaterJugMinOps();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            StringTokenizer t = new StringTokenizer(line);
            int j1 = Integer.parseInt(t.nextToken());
            int j2 = Integer.parseInt(t.nextToken());
            int tg = Integer.parseInt(t.nextToken());
            List<Step> path = solver.solve(j1, j2, tg);
            if (path.isEmpty()) { System.out.println("UNREACHABLE"); continue; }
            System.out.println("ops=" + (path.size() - 1));
            for (Step s : path) System.out.println("  " + s);
        }
    }

    private static void runDemos() {
        WaterJugMinOps solver = new WaterJugMinOps();

        // ---- Spec example ----
        runCase(solver, 3, 5, 4, /*reachable=*/true,  /*minOps=*/6);

        // ---- Classic LC variants ----
        runCase(solver, 2, 6, 5, false, -1);          // gcd(2,6)=2; 5 % 2 != 0
        runCase(solver, 1, 2, 3, false, -1);          // target > max
        runCase(solver, 4, 6, 8, false, -1);          // 8 > 6
        runCase(solver, 7, 5, 6, true,  -1);          // reachable; min ops not asserted
        runCase(solver, 5, 3, 4, true,  6);           // symmetric to spec

        // ---- Edge: target == 0 ----
        runCase(solver, 3, 5, 0, true, 0);
        // ---- Edge: target is exactly one of the capacities ----
        runCase(solver, 3, 5, 5, true, 1);            // single fill of jug2
        runCase(solver, 3, 5, 3, true, 1);            // single fill of jug1
        // ---- Edge: one jug has capacity 0 ----
        runCase(solver, 0, 5, 5, true, 1);
        runCase(solver, 0, 5, 3, false, -1);          // gcd(0,5)=5; 3%5 != 0
        // ---- Edge: both zero ----
        runCase(solver, 0, 0, 0, true, 0);
        runCase(solver, 0, 0, 1, false, -1);

        // ---- Stress: medium board ----
        long t0 = System.nanoTime();
        List<Step> big = solver.solve(997, 1009, 13);    // two primes -> gcd=1, reachable
        long ms = (System.nanoTime() - t0) / 1_000_000;
        check("stress 997 x 1009 -> 13", !big.isEmpty(), true);
        System.out.println("Stress 997x1009 target=13: ops=" + (big.size() - 1) + " in " + ms + " ms");
    }

    private static void runCase(WaterJugMinOps solver, int j1, int j2, int target,
                                boolean expectReachable, int expectedMinOpsOrMinusOne) {
        List<Step> path = solver.solve(j1, j2, target);
        boolean reachable = !path.isEmpty();
        String label = "j1=" + j1 + " j2=" + j2 + " target=" + target;
        check(label + " reachable", reachable, expectReachable);
        if (expectedMinOpsOrMinusOne >= 0 && reachable) {
            int ops = path.size() - 1;                  // first entry is START
            check(label + " minOps=" + expectedMinOpsOrMinusOne, ops, expectedMinOpsOrMinusOne);
        }
        if (reachable) verifyPath(label, j1, j2, target, path);
    }

    /** Validate that each consecutive pair of states is a single legal op. */
    private static void verifyPath(String label, int j1, int j2, int target, List<Step> path) {
        if (path.isEmpty()) { check(label + " path valid", false, true); return; }
        Step s0 = path.get(0);
        if (s0.a != 0 || s0.b != 0 || s0.action != Action.START) {
            check(label + " path starts at (0,0)/start", false, true); return;
        }
        for (int i = 1; i < path.size(); i++) {
            Step prev = path.get(i - 1), cur = path.get(i);
            if (!legalTransition(prev.a, prev.b, cur.a, cur.b, cur.action, j1, j2)) {
                check(label + " step " + i + " legal: " + prev + " -> " + cur, false, true);
                return;
            }
        }
        Step last = path.get(path.size() - 1);
        check(label + " final has target", last.a == target || last.b == target, true);
    }

    private static boolean legalTransition(int a, int b, int na, int nb, Action act, int j1, int j2) {
        switch (act) {
            case FILL_J1:     return na == j1 && nb == b && a != j1;
            case FILL_J2:     return na == a && nb == j2 && b != j2;
            case EMPTY_J1:    return na == 0 && nb == b && a != 0;
            case EMPTY_J2:    return na == a && nb == 0 && b != 0;
            case POUR_1_TO_2: { int t = Math.min(a, j2 - b); return t > 0 && na == a - t && nb == b + t; }
            case POUR_2_TO_1: { int t = Math.min(b, j1 - a); return t > 0 && na == a + t && nb == b - t; }
            default:          return false;
        }
    }

    private static void check(String label, int got, int expected) {
        boolean ok = got == expected;
        System.out.println((ok ? "OK   " : "FAIL ") + label + "  got=" + got + " expected=" + expected);
    }
    private static void check(String label, boolean got, boolean expected) {
        boolean ok = got == expected;
        System.out.println((ok ? "OK   " : "FAIL ") + label + "  got=" + got + " expected=" + expected);
    }
}
