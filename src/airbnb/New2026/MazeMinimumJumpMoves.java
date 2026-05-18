package airbnb.New2026;
/*
Maze - Minimum Moves with Jump Distance k.

Grid n*m with 0 = empty, 1 = obstacle.  Start at (0,0), target (n-1,m-1).
In one move HackerMan can travel up to k cells in any one of the four
cardinal directions, but the entire path between start and end of the
jump must be empty (no obstacles).  Return the minimum number of moves
to reach (n-1, m-1), or -1 if unreachable.

Examples
  maze = [[0,0],[1,0]],            k = 2  ->  2
  maze = [[0,0,0],[0,0,0],[0,0,0]], k = 100 -> 1   (one big jump if same row/col)
  maze = [[0,1,0],[0,1,0],[0,1,0]], k = 1   -> -1  (column of obstacles)
  maze[0][0] == 1                  ->  -1
  start == target (1x1 with 0)     ->  0

Constraints
  1 <= n, m, k <= 100
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Random;

/*
Algorithm: plain BFS — every legal move has weight 1.

  Each cell has up to 4 * k "neighbors" (1..k steps in each direction
  before hitting a wall or obstacle).  We mark visited on enqueue, not
  on dequeue, so each cell is queued at most once.

  Walking a direction:
    Start from (r, c), step one cell at a time.  As soon as we hit a
    grid boundary or an obstacle, stop the WHOLE direction — every
    further x is also unreachable from this jump (path is blocked).
    Each empty cell visited in the walk is a valid landing spot that
    counts as a single move from (r, c); enqueue it if unvisited.

  Worst-case work per BFS pop: O(4 * k).  Total: O(n * m * k).
  At n, m, k <= 100 that's <= 4 * 10^6 cell touches.  Plenty fast.

  An optimization we DON'T need at these constraints (mentioned for
  completeness): once you walk past a cell already visited in the
  same direction, the rest of that direction was already reachable
  from that cell with the same or fewer moves, so you can stop.
  That turns the per-cell work amortized O(4), total O(n * m).

Why standard BFS suffices (not 0-1 BFS or Dijkstra):
  All edges have unit weight.  Length-of-jump doesn't change the cost.

Correctness of "mark visited on enqueue":
  BFS visits cells in non-decreasing distance order, so the first time
  a cell is reached is via a shortest path.  Re-queueing later costs
  more, never less, so we skip it.
*/
public class MazeMinimumJumpMoves {

    private static final int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};

    public int getMinimumMoves(int[][] maze, int k) {
        if (maze == null || maze.length == 0 || maze[0].length == 0) return -1;
        if (k <= 0) throw new IllegalArgumentException("k must be positive: " + k);
        int n = maze.length, m = maze[0].length;

        // Trivial / hopeless cases.
        if (maze[0][0] == 1 || maze[n - 1][m - 1] == 1) return -1;
        if (n == 1 && m == 1) return 0;

        int[][] dist = new int[n][m];
        for (int[] row : dist) Arrays.fill(row, -1);
        dist[0][0] = 0;

        Deque<int[]> q = new ArrayDeque<>();
        q.offer(new int[]{0, 0});

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int r = cur[0], c = cur[1];
            int d = dist[r][c];

            for (int[] dir : DIRS) {
                int dr = dir[0], dc = dir[1];
                int nr = r, nc = c;
                for (int step = 1; step <= k; step++) {
                    nr += dr; nc += dc;
                    if (nr < 0 || nr >= n || nc < 0 || nc >= m) break;
                    if (maze[nr][nc] == 1) break;                  // blocked; rest of direction is unreachable in this jump
                    if (dist[nr][nc] != -1) continue;              // already reached via a >=-as-short path; keep walking
                    dist[nr][nc] = d + 1;
                    if (nr == n - 1 && nc == m - 1) return d + 1;  // first time we land on target is optimal
                    q.offer(new int[]{nr, nc});
                }
            }
        }
        return -1;
    }

    /* --------------------------- O((nm)^2) brute force for tests --------------------------- */

    /** Floyd-Warshall over all-pairs reachability under the same jump rule, for cross-check. */
    int getMinimumMovesBrute(int[][] maze, int k) {
        int n = maze.length, m = maze[0].length;
        if (maze[0][0] == 1 || maze[n-1][m-1] == 1) return -1;
        if (n == 1 && m == 1) return 0;
        int V = n * m;
        int[][] d = new int[V][V];
        for (int[] r : d) Arrays.fill(r, Integer.MAX_VALUE / 2);
        for (int i = 0; i < V; i++) d[i][i] = 0;

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < m; c++) {
                if (maze[r][c] == 1) continue;
                for (int[] dir : DIRS) {
                    int nr = r, nc = c;
                    for (int step = 1; step <= k; step++) {
                        nr += dir[0]; nc += dir[1];
                        if (nr < 0 || nr >= n || nc < 0 || nc >= m) break;
                        if (maze[nr][nc] == 1) break;
                        d[r * m + c][nr * m + nc] = 1;
                    }
                }
            }
        }
        // Floyd-Warshall
        for (int kk = 0; kk < V; kk++)
            for (int i = 0; i < V; i++)
                for (int j = 0; j < V; j++)
                    if (d[i][kk] + d[kk][j] < d[i][j]) d[i][j] = d[i][kk] + d[kk][j];

        int dst = d[0][V - 1];
        return dst >= Integer.MAX_VALUE / 4 ? -1 : dst;
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
     *   line 1: n m k
     *   next n lines: m ints separated by spaces
     */
    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] hdr = br.readLine().trim().split("\\s+");
        int n = Integer.parseInt(hdr[0]);
        int m = Integer.parseInt(hdr[1]);
        int k = Integer.parseInt(hdr[2]);
        int[][] maze = new int[n][m];
        for (int i = 0; i < n; i++) {
            String[] tok = br.readLine().trim().split("\\s+");
            for (int j = 0; j < m; j++) maze[i][j] = Integer.parseInt(tok[j]);
        }
        System.out.println(new MazeMinimumJumpMoves().getMinimumMoves(maze, k));
    }

    private static void runDemos() {
        MazeMinimumJumpMoves solver = new MazeMinimumJumpMoves();

        // Spec example.
        check(solver, new int[][]{
                {0, 0},
                {1, 0}
        }, 2, 2);
        // From (0,0): jump right -> (0,1).  Jump down -> (1,1).  2 moves.

        // Big k clears it in 1 or 2 jumps on a clear board.
        check(solver, new int[][]{
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        }, 100, 2);   // (0,0) -> (0,2) -> (2,2); cannot do diagonally in one move.

        // Single cell.
        check(solver, new int[][]{{0}}, 5, 0);

        // Start or target blocked.
        check(solver, new int[][]{{1, 0}, {0, 0}}, 1, -1);
        check(solver, new int[][]{{0, 0}, {0, 1}}, 1, -1);

        // Column of obstacles separates start from target.
        check(solver, new int[][]{
                {0, 1, 0},
                {0, 1, 0},
                {0, 1, 0}
        }, 1, -1);

        // k=1 is plain BFS; classic Manhattan distance on a clear grid.
        check(solver, new int[][]{
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        }, 1, 4);

        // Snake around an obstacle.
        // 0 0 0
        // 1 1 0
        // 0 0 0
        // With k=1: only path is across the top then down right column = 4 moves.
        // With k=2: same, the wall is wide enough to require going around. 4 -> 3?
        // (0,0)->(0,2) one move (k>=2). (0,2)->(2,2) one move. = 2 moves.
        check(solver, new int[][]{
                {0, 0, 0},
                {1, 1, 0},
                {0, 0, 0}
        }, 2, 2);
        check(solver, new int[][]{
                {0, 0, 0},
                {1, 1, 0},
                {0, 0, 0}
        }, 1, 4);

        // ---- Random fuzz against Floyd-Warshall on small grids ----
        Random rnd = new Random(2026);
        int trials = 200, fails = 0;
        for (int t = 0; t < trials; t++) {
            int n = 1 + rnd.nextInt(5);
            int m = 1 + rnd.nextInt(5);
            int[][] g = new int[n][m];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < m; j++)
                    g[i][j] = (rnd.nextDouble() < 0.25) ? 1 : 0;
            // Ensure start and end aren't always blocked.
            int k = 1 + rnd.nextInt(5);
            int fast  = solver.getMinimumMoves(g, k);
            int brute = solver.getMinimumMovesBrute(g, k);
            if (fast != brute) {
                fails++;
                System.out.println("MISMATCH n=" + n + " m=" + m + " k=" + k
                        + " fast=" + fast + " brute=" + brute
                        + " maze=" + Arrays.deepToString(g));
            }
        }
        System.out.println("Random cross-check: " + (trials - fails) + "/" + trials + " ok");

        // ---- Stress: max constraints ----
        int N = 100, M = 100, K = 100;
        int[][] big = new int[N][M];
        Random brnd = new Random(7);
        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++)
                big[i][j] = (brnd.nextDouble() < 0.15) ? 1 : 0;
        big[0][0] = 0; big[N - 1][M - 1] = 0;
        long t0 = System.nanoTime();
        int ans = solver.getMinimumMoves(big, K);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("Stress n=" + N + " m=" + M + " k=" + K + ": ans=" + ans + " in " + ms + " ms");
    }

    private static void check(MazeMinimumJumpMoves solver, int[][] maze, int k, int expected) {
        int fast = solver.getMinimumMoves(maze, k);
        int brute = solver.getMinimumMovesBrute(maze, k);
        boolean ok = fast == expected && brute == expected;
        System.out.println((ok ? "OK   " : "FAIL ")
                + "k=" + k + " expected=" + expected + " fast=" + fast + " brute=" + brute
                + " maze=" + Arrays.deepToString(maze));
    }
}
