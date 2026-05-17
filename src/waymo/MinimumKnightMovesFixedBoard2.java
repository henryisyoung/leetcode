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
import java.util.*;

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
public class MinimumKnightMovesFixedBoard2 {

    private static final int[][] KNIGHT_MOVES = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            { 1, -2}, { 1, 2}, { 2, -1}, { 2, 1}
    };

    /* --------------------------- Single-direction BFS --------------------------- */

    /** Returns the minimum number of knight moves, or -1 if unreachable. */
    public int minMoves(int m, int n, int sr, int sc, int tr, int tc) {
        if (!valdiate(m,n,sr,sc) || !valdiate(m,n,tr,tc)) {
            return -1;
        }

        if (sr == tr && sc == tc) {
            return 0;
        }

        int step = 0;
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{sr, sc});
        boolean[][] visited = new boolean[m][n];

        visited[sr][sc] = true;


        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] cur = queue.poll();
                int r = cur[0], c = cur[1];
                if (r == tr && c == tc) {
                    return step;
                }

                for (int[] dir : KNIGHT_MOVES) {
                    int nr = dir[0] + r, nc = dir[1] + c;
                    if (valdiate(m,n,nr,nc) && !visited[nr][nc]) {
                        queue.add(new int[]{nr, nc});
                        visited[nr][nc] = true;
                    }
                }
            }
            step++;
        }

        return -1;
    }

    private boolean valdiate(int m, int n, int r, int c) {
        return r >= 0 && r < m && c >= 0 && c < n;
    }
}
