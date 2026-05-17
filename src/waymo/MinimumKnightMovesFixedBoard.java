package waymo;
/*
Minimum Knight Moves on a fixed-size board.

Given an m x n chess board, a starting cell (sr, sc), and a target cell
(tr, tc), return the minimum number of knight moves needed to go from
start to target — or -1 if the target is unreachable.

A knight moves in an "L": two squares along one axis, one along the
perpendicular.  Out-of-bounds moves are not allowed (this is the key
difference from LeetCode 1197, which is on an infinite plane).

Stdin format
  Line 1:  m n
  Line 2:  sr sc tr tc
  Output:  the minimum number of moves, or -1 if unreachable.

Constraints (assumed)
  1 <= m, n <= 1000
  0 <= sr, tr < m;  0 <= sc, tc < n

Examples
  8x8, (0,0) -> (7,7)        : 6
  8x8, (0,0) -> (0,1)        : 3
  3x3, (0,0) -> (1,1)        : -1   (center is unreachable on 3x3)
  2x2, (0,0) -> (1,1)        : -1   (every knight move leaves the board)
  any, (sr,sc) == (tr,tc)    : 0
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Random;
import java.util.StringTokenizer;

/*
Algorithm: unweighted shortest-path on a grid graph -> BFS.

  Each cell is a node; each cell has up to 8 knight-neighbors (only those
  that stay in bounds).  Every move costs 1, so BFS by layers gives the
  shortest path.  We use a flat int[] visited (encoded as r*n + c) and
  level-based BFS to track depth without storing it per node.

  Early exit: when we are about to enqueue the target, we know it sits
  at depth+1, so we can return immediately without dequeuing it.

  Bonus: a bidirectional BFS variant that expands the smaller frontier
  each iteration.  For large boards with long paths it can be ~2x
  faster; for small boards it's a wash.  Cross-checked against the
  single-direction BFS by random fuzz, plus both are checked against an
  all-pairs Floyd-Warshall on tiny boards.

Complexity
  Single BFS         : O(m*n) time, O(m*n) memory.
  Bidirectional BFS  : same asymptotic, lower constant on long paths.
  Floyd (test only)  : O((m*n)^3) — used only on boards <= 6x6.
*/
public class MinimumKnightMovesFixedBoard {

    private static final int[][] KNIGHT_MOVES = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            { 1, -2}, { 1, 2}, { 2, -1}, { 2, 1}
    };

    /* --------------------------- Single-direction BFS --------------------------- */

    /** Returns the minimum number of knight moves, or -1 if unreachable. */
    public int minMoves(int m, int n, int sr, int sc, int tr, int tc) {
        validate(m, n, sr, sc, tr, tc);
        if (sr == tr && sc == tc) return 0;

        boolean[] visited = new boolean[m * n];
        Deque<Integer> q = new ArrayDeque<>();
        int s = sr * n + sc;
        q.offer(s);
        visited[s] = true;

        int depth = 0;
        while (!q.isEmpty()) {
            depth++;
            int size = q.size();
            for (int i = 0; i < size; i++) {
                int code = q.poll();
                int r = code / n, c = code % n;
                for (int[] d : KNIGHT_MOVES) {
                    int nr = r + d[0], nc = c + d[1];
                    if (nr < 0 || nr >= m || nc < 0 || nc >= n) continue;
                    int ncode = nr * n + nc;
                    if (visited[ncode]) continue;
                    if (nr == tr && nc == tc) return depth;
                    visited[ncode] = true;
                    q.offer(ncode);
                }
            }
        }
        return -1;
    }

    /* --------------------------- Bidirectional BFS --------------------------- */

    /**
     * Expand the smaller frontier each step; stop when one side discovers a
     * cell already visited by the other.  For knight moves the graph is
     * undirected (every move is reversible), so the meeting-cell trick gives
     * the correct optimum.
     */
    public int minMovesBidirectional(int m, int n, int sr, int sc, int tr, int tc) {
        validate(m, n, sr, sc, tr, tc);
        if (sr == tr && sc == tc) return 0;

        int N = m * n;
        int s = sr * n + sc, t = tr * n + tc;
        int[] distS = new int[N]; Arrays.fill(distS, -1); distS[s] = 0;
        int[] distT = new int[N]; Arrays.fill(distT, -1); distT[t] = 0;
        Deque<Integer> qS = new ArrayDeque<>(); qS.offer(s);
        Deque<Integer> qT = new ArrayDeque<>(); qT.offer(t);

        while (!qS.isEmpty() && !qT.isEmpty()) {
            int hit;
            if (qS.size() <= qT.size()) {
                hit = expandLayer(qS, distS, distT, m, n);
            } else {
                hit = expandLayer(qT, distT, distS, m, n);
            }
            if (hit != -1) return hit;
        }
        return -1;
    }

    /** Expand one BFS layer; return the smallest meeting total seen in this layer, else -1. */
    private int expandLayer(Deque<Integer> q, int[] dist, int[] otherDist, int m, int n) {
        int size = q.size();
        int best = -1;
        for (int i = 0; i < size; i++) {
            int code = q.poll();
            int r = code / n, c = code % n;
            int curDist = dist[code];
            for (int[] d : KNIGHT_MOVES) {
                int nr = r + d[0], nc = c + d[1];
                if (nr < 0 || nr >= m || nc < 0 || nc >= n) continue;
                int ncode = nr * n + nc;
                if (dist[ncode] != -1) continue;
                dist[ncode] = curDist + 1;
                if (otherDist[ncode] != -1) {
                    int total = dist[ncode] + otherDist[ncode];
                    if (best == -1 || total < best) best = total;
                }
                q.offer(ncode);
            }
        }
        return best;
    }

    /* --------------------------- Validation --------------------------- */

    private static void validate(int m, int n, int sr, int sc, int tr, int tc) {
        if (m <= 0 || n <= 0) throw new IllegalArgumentException("m, n must be positive");
        if (sr < 0 || sr >= m || tr < 0 || tr >= m) throw new IllegalArgumentException("row out of bounds");
        if (sc < 0 || sc >= n || tc < 0 || tc >= n) throw new IllegalArgumentException("col out of bounds");
    }

    /* --------------------------- Floyd-Warshall reference (tests only) --------------------------- */

    /** Returns dist[s*n+c][t*n+c'] for every pair of cells.  O((m*n)^3); only used on tiny boards. */
    int minMovesFloyd(int m, int n, int sr, int sc, int tr, int tc) {
        validate(m, n, sr, sc, tr, tc);
        int N = m * n;
        int INF = Integer.MAX_VALUE / 2;
        int[][] dist = new int[N][N];
        for (int[] row : dist) Arrays.fill(row, INF);
        for (int i = 0; i < N; i++) dist[i][i] = 0;
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                int u = r * n + c;
                for (int[] d : KNIGHT_MOVES) {
                    int nr = r + d[0], nc = c + d[1];
                    if (nr < 0 || nr >= m || nc < 0 || nc >= n) continue;
                    dist[u][nr * n + nc] = 1;
                }
            }
        }
        for (int k = 0; k < N; k++) {
            for (int i = 0; i < N; i++) {
                if (dist[i][k] >= INF) continue;
                for (int j = 0; j < N; j++) {
                    int via = dist[i][k] + dist[k][j];
                    if (via < dist[i][j]) dist[i][j] = via;
                }
            }
        }
        int d = dist[sr * n + sc][tr * n + tc];
        return d >= INF ? -1 : d;
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
        int m = Integer.parseInt(st.nextToken());
        int n = Integer.parseInt(st.nextToken());
        st = new StringTokenizer(br.readLine());
        int sr = Integer.parseInt(st.nextToken());
        int sc = Integer.parseInt(st.nextToken());
        int tr = Integer.parseInt(st.nextToken());
        int tc = Integer.parseInt(st.nextToken());
        System.out.println(new MinimumKnightMovesFixedBoard().minMoves(m, n, sr, sc, tr, tc));
    }

    /* --------------------------- Demo + tests --------------------------- */

    private static void runDemos() {
        MinimumKnightMovesFixedBoard solver = new MinimumKnightMovesFixedBoard();

        // Trivial: source == target.
        check(solver, 1, 1, 0, 0, 0, 0, 0);
        check(solver, 8, 8, 3, 4, 3, 4, 0);

        // 1x1 — single cell.
        // (covered above)

        // 2x2 — knight cannot move at all (every move leaves the board).
        check(solver, 2, 2, 0, 0, 1, 1, -1);
        check(solver, 2, 2, 0, 0, 0, 1, -1);

        // 3x3 — center cell is unreachable from any other cell.
        check(solver, 3, 3, 0, 0, 1, 1, -1);
        check(solver, 3, 3, 1, 1, 0, 0, -1);
        // 3x3 corner-to-corner is reachable in 4.
        check(solver, 3, 3, 0, 0, 2, 2, 4);

        // 8x8 famous cases.
        check(solver, 8, 8, 0, 0, 7, 7, 6);   // diagonal corner-to-corner
        check(solver, 8, 8, 0, 0, 0, 1, 3);   // adjacent on row — knight can't go directly
        check(solver, 8, 8, 0, 0, 1, 1, 4);   // diagonal adjacent — corner trap
        check(solver, 8, 8, 0, 0, 2, 1, 1);   // a single L-move

        // Larger board — let BFS define expected, cross-checked against Floyd.
        int[] sizes = {5, 6};
        Random rnd = new Random(13);
        int mismatches = 0;
        for (int m : sizes) {
            for (int n : sizes) {
                for (int trial = 0; trial < 20; trial++) {
                    int sr = rnd.nextInt(m), sc = rnd.nextInt(n);
                    int tr = rnd.nextInt(m), tc = rnd.nextInt(n);
                    int bfs = solver.minMoves(m, n, sr, sc, tr, tc);
                    int bidi = solver.minMovesBidirectional(m, n, sr, sc, tr, tc);
                    int floyd = solver.minMovesFloyd(m, n, sr, sc, tr, tc);
                    if (bfs != bidi || bfs != floyd) {
                        mismatches++;
                        System.out.println("MISMATCH " + m + "x" + n
                                + " (" + sr + "," + sc + ")->(" + tr + "," + tc + ")"
                                + " bfs=" + bfs + " bidi=" + bidi + " floyd=" + floyd);
                    }
                }
            }
        }
        System.out.println("Cross-check (BFS vs bidi vs Floyd): " + (80 - mismatches) + "/80 ok");

        // Random fuzz on bigger boards: BFS vs bidirectional only (Floyd too slow at 50x50).
        int big = 0, bigOk = 0;
        for (int trial = 0; trial < 200; trial++) {
            int m = 4 + rnd.nextInt(47);   // 4..50
            int n = 4 + rnd.nextInt(47);
            int sr = rnd.nextInt(m), sc = rnd.nextInt(n);
            int tr = rnd.nextInt(m), tc = rnd.nextInt(n);
            int bfs = solver.minMoves(m, n, sr, sc, tr, tc);
            int bidi = solver.minMovesBidirectional(m, n, sr, sc, tr, tc);
            big++;
            if (bfs == bidi) bigOk++;
            else System.out.println("BIDI MISMATCH " + m + "x" + n
                    + " (" + sr + "," + sc + ")->(" + tr + "," + tc + ")"
                    + " bfs=" + bfs + " bidi=" + bidi);
        }
        System.out.println("BFS vs bidirectional on 4..50 boards: " + bigOk + "/" + big + " ok");

        // Performance: 1000x1000 corner-to-corner.
        int N = 1000;
        long t0 = System.nanoTime();
        int ans = solver.minMoves(N, N, 0, 0, N - 1, N - 1);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("Stress " + N + "x" + N + " (0,0)->(" + (N-1) + "," + (N-1) + "): "
                + ans + " moves in " + ms + " ms (single BFS)");

        long t1 = System.nanoTime();
        int ans2 = solver.minMovesBidirectional(N, N, 0, 0, N - 1, N - 1);
        long ms2 = (System.nanoTime() - t1) / 1_000_000;
        System.out.println("Stress " + N + "x" + N + " (0,0)->(" + (N-1) + "," + (N-1) + "): "
                + ans2 + " moves in " + ms2 + " ms (bidirectional)");
    }

    private static void check(MinimumKnightMovesFixedBoard solver,
                              int m, int n, int sr, int sc, int tr, int tc, int expected) {
        int got = solver.minMoves(m, n, sr, sc, tr, tc);
        int gotBidi = solver.minMovesBidirectional(m, n, sr, sc, tr, tc);
        boolean ok = got == expected && gotBidi == expected;
        System.out.println((ok ? "OK   " : "FAIL ")
                + m + "x" + n + " (" + sr + "," + sc + ")->(" + tr + "," + tc + ")"
                + " expected=" + expected + " bfs=" + got + " bidi=" + gotBidi);
    }
}
