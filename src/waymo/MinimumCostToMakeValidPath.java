package waymo;
/*
LeetCode 1368: Minimum Cost to Make at Least One Valid Path in a Grid.

Given an m x n grid where grid[i][j] is one of:
    1 = go right    (i, j+1)
    2 = go left     (i, j-1)
    3 = go down     (i+1, j)
    4 = go up       (i-1, j)
Starting at (0, 0), follow the signs to walk the grid. You may change the
sign in any cell to any of {1,2,3,4} at cost 1 (each cell can be changed
at most once). Return the minimum total cost so that there exists a valid
path from (0, 0) to (m-1, n-1).

Examples
  grid = [[1,1,1,1],[2,2,2,2],[1,1,1,1],[2,2,2,2]]  -> 3
  grid = [[1,1,3],[3,2,2],[1,1,4]]                  -> 0
  grid = [[1,2],[4,3]]                              -> 1

Constraints
  1 <= m, n <= 100
  1 <= grid[i][j] <= 4
*/

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.PriorityQueue;

/*
Algorithm: 0-1 BFS on the implicit graph of cells.

  - Edge from (i, j) in the direction grid[i][j] points has weight 0
    (we already follow the sign for free).
  - Edges to the other 3 neighbors have weight 1 (we have to change the
    sign in (i, j) once).
  - Because edge weights are only {0, 1}, a deque-based BFS settles each
    cell in O(1) amortized: 0-edges go to the front, 1-edges to the back.
    First time we pop a cell, its dist is final (same invariant as Dijkstra).

  dist[i][j]   minimum cost to reach (i, j); INF initially, dist[0][0] = 0
  deque        cells to expand, ordered by current cost
  pop          (i, j); if cost > dist[i][j] skip (stale)
  expand       for each of the 4 neighbors:
                 w = (direction matches grid[i][j]) ? 0 : 1
                 nd = cost + w
                 if nd < dist[ni][nj]: dist[ni][nj] = nd
                   if w == 0 push front, else push back

Why 0-1 BFS and not plain Dijkstra:
  Both are correct. 0-1 BFS is O(m*n) with a deque vs O(m*n*log(m*n))
  with a heap, and avoids the heap constant factor. Dijkstra is kept
  below as a reference / sanity check.

Direction encoding (kept consistent with the problem statement):
  dirs[k] = {dr, dc} for sign value k+1, so grid[i][j] - 1 is the index
  of the "free" direction at cell (i, j).

Complexity
  Time:   O(m * n)            (0-1 BFS, each cell pushed at most a few times)
  Memory: O(m * n)
*/
public class MinimumCostToMakeValidPath {

    // index = sign value - 1.  1=right, 2=left, 3=down, 4=up.
    private static final int[][] DIRS = {
            {0, 1},   // 1 right
            {0, -1},  // 2 left
            {1, 0},   // 3 down
            {-1, 0},  // 4 up
    };

    /** 0-1 BFS solution. */
    public int minCost(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) return 0;
        int m = grid.length, n = grid[0].length;

        int[][] dist = new int[m][n];
        for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);
        dist[0][0] = 0;

        // Entry: {row, col, cost}.  Cost stored on the entry to skip stale pops.
        Deque<int[]> dq = new ArrayDeque<>();
        dq.offerFirst(new int[]{0, 0, 0});

        while (!dq.isEmpty()) {
            int[] cur = dq.pollFirst();
            int r = cur[0], c = cur[1], d = cur[2];
            if (d > dist[r][c]) continue;             // stale, a cheaper copy was already settled
            if (r == m - 1 && c == n - 1) return d;   // first pop is optimal in 0-1 BFS

            int signIdx = grid[r][c] - 1;             // the "free" direction at this cell
            for (int k = 0; k < 4; k++) {
                int nr = r + DIRS[k][0];
                int nc = c + DIRS[k][1];
                if (nr < 0 || nr >= m || nc < 0 || nc >= n) continue;
                int w = (k == signIdx) ? 0 : 1;
                int nd = d + w;
                if (nd < dist[nr][nc]) {
                    dist[nr][nc] = nd;
                    // 0-weight edges go to the front to preserve the cost ordering.
                    if (w == 0) dq.offerFirst(new int[]{nr, nc, nd});
                    else        dq.offerLast(new int[]{nr, nc, nd});
                }
            }
        }
        return dist[m - 1][n - 1];                     // unreachable would be INF, but the grid is always connected
    }

    /* --------------------------- Dijkstra reference for tests --------------------------- */

    /** Plain Dijkstra; same semantics, used to cross-check the 0-1 BFS. */
    int minCostDijkstra(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[][] dist = new int[m][n];
        for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);
        dist[0][0] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[2], b[2]));
        pq.offer(new int[]{0, 0, 0});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int r = cur[0], c = cur[1], d = cur[2];
            if (d > dist[r][c]) continue;
            if (r == m - 1 && c == n - 1) return d;

            int signIdx = grid[r][c] - 1;
            for (int k = 0; k < 4; k++) {
                int nr = r + DIRS[k][0];
                int nc = c + DIRS[k][1];
                if (nr < 0 || nr >= m || nc < 0 || nc >= n) continue;
                int nd = d + ((k == signIdx) ? 0 : 1);
                if (nd < dist[nr][nc]) {
                    dist[nr][nc] = nd;
                    pq.offer(new int[]{nr, nc, nd});
                }
            }
        }
        return dist[m - 1][n - 1];
    }

    /* --------------------------- demo / tests --------------------------- */

    public static void main(String[] args) {
        MinimumCostToMakeValidPath solver = new MinimumCostToMakeValidPath();

        check(solver, new int[][]{
                {1,1,1,1},
                {2,2,2,2},
                {1,1,1,1},
                {2,2,2,2}}, 3);

        check(solver, new int[][]{
                {1,1,3},
                {3,2,2},
                {1,1,4}}, 0);

        check(solver, new int[][]{
                {1,2},
                {4,3}}, 1);

        // 1x1: already at the target.
        check(solver, new int[][]{{1}}, 0);

        // Single row, sign points right the whole way → free.
        check(solver, new int[][]{{1,1,1,1,1}}, 0);

        // Single row, all signs point left → must redirect every cell except the last.
        check(solver, new int[][]{{2,2,2,2,2}}, 4);

        // Single column, all down → free.
        check(solver, new int[][]{{3},{3},{3},{3}}, 0);

        // Single column, all up → must redirect every cell except the last.
        check(solver, new int[][]{{4},{4},{4},{4}}, 3);

        // 2x2 zigzag: 1 -> right blocked by edge, must redirect (0,0).
        check(solver, new int[][]{{3,4},{1,2}}, 0);  // 1 down to (1,0), then right to (1,1) — both free
    }

    private static void check(MinimumCostToMakeValidPath solver, int[][] grid, int expected) {
        int got = solver.minCost(grid);
        int dj = solver.minCostDijkstra(grid);
        boolean ok = got == expected && dj == expected;
        System.out.println((ok ? "OK   " : "FAIL ")
                + "expected=" + expected + " bfs01=" + got + " dijkstra=" + dj
                + " grid=" + Arrays.deepToString(grid));
    }
}
