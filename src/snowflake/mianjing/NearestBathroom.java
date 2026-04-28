package snowflake.mianjing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/*
Problem
-------
You are given an m x n grid representing an office floor. Each cell is one of:
   'B' — a Bathroom
   'D' — a Desk
   '.' — open floor (walkable empty space)
   '#' — a wall (impassable, optional)

For every Desk, find the shortest 4-directional walking distance to the
nearest Bathroom. Return the answer as either:
  - a full distance grid (-1 = unreachable / blocked), or
  - a list of {row, col, distance} entries, one per Desk.

Approach: multi-source BFS
--------------------------
The key trick is that we DON'T BFS from each desk. Instead, we BFS *outward
from every bathroom simultaneously*:

  1. Push every 'B' cell into the queue with distance 0.
  2. BFS in lockstep — the first time any cell is reached, it's reached by
     the closest bathroom (because all sources start at depth 0 in the same
     queue).
  3. After BFS, the distance grid is fully populated; just look up each desk.

This is O(m*n) total work — far better than running |B| or |D| separate
BFS searches, which would be O(B * m * n) or O(D * m * n).

Why it's correct
----------------
BFS expands cells in non-decreasing distance order. With multiple sources at
distance 0, the first time we visit cell c, the parent that reached us came
from the closest source — any closer source would have reached c earlier,
which is impossible since BFS visits cells in distance order.

Time:  O(m * n)
Space: O(m * n) for the distance grid + queue

// 对于每个desk，都输出最近的bathroom的距离
 */
public class NearestBathroom {

    private static final int[][] DIRS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    /**
     * Returns a grid where dist[i][j] is the shortest distance from (i,j) to
     * the nearest 'B'. -1 means unreachable (blocked off by walls or no 'B' in
     * the grid). Walls ('#') keep -1.
     */
    public int[][] distanceGrid(char[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[][] dist = new int[m][n];
        for (int[] row : dist) Arrays.fill(row, -1);

        Deque<int[]> queue = new ArrayDeque<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 'B') {
                    dist[i][j] = 0;
                    queue.offer(new int[]{i, j});
                }
            }
        }

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1];
            for (int[] d : DIRS) {
                int nr = r + d[0], nc = c + d[1];
                if (nr < 0 || nr >= m || nc < 0 || nc >= n) continue;
                if (dist[nr][nc] != -1) continue;          // already visited
                if (grid[nr][nc] == '#') continue;          // wall
                dist[nr][nc] = dist[r][c] + 1;
                queue.offer(new int[]{nr, nc});
            }
        }
        return dist;
    }

    /**
     * Convenience: returns one entry per 'D' cell as int[]{row, col, distance}.
     * distance is -1 if the desk is unreachable.
     */
    public List<int[]> deskDistances(char[][] grid) {
        int[][] dist = distanceGrid(grid);
        List<int[]> out = new ArrayList<>();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 'D') {
                    out.add(new int[]{i, j, dist[i][j]});
                }
            }
        }
        return out;
    }

    // ============================================================
    // Demo / tests
    // ============================================================
    private static char[][] grid(String... rows) {
        char[][] g = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) g[i] = rows[i].toCharArray();
        return g;
    }

    public static void main(String[] args) {
        NearestBathroom s = new NearestBathroom();

        // Case 1: one B at (0,0), one D at (2,2). Manhattan distance 4.
        char[][] g1 = grid(
                "B..",
                "...",
                "..D"
        );
        printDeskDistances("case 1", s.deskDistances(g1));   // [(2,2,4)]

        // Case 2: a desk between two bathrooms — picks the nearer.
        // B at (0,0) and (2,3); D at (0,3).  d(0,3)→(0,0)=3, →(2,3)=2 → min 2.
        char[][] g2 = grid(
                "B..D",
                "....",
                "...B"
        );
        printDeskDistances("case 2", s.deskDistances(g2));   // [(0,3,2)]

        // Case 3: multiple desks, multiple bathrooms.
        //   B . . D
        //   . . D .
        //   D . . B
        // Desks at (0,3),(1,2),(2,0). Bathrooms at (0,0),(2,3).
        //   (0,3): min(3, 2) = 2
        //   (1,2): min(3, 2) = 2
        //   (2,0): min(2, 5) = 2
        char[][] g3 = grid(
                "B..D",
                "..D.",
                "D..B"
        );
        printDeskDistances("case 3", s.deskDistances(g3));

        // Case 4: walls force a detour.
        //   B # . D       direct distance is 3 but '#' wall blocks it,
        //   . # . .       so the path must go around through the bottom row.
        //   . . . .       (0,0) → (1,0) → (2,0) → (2,1) → (2,2) → (2,3) → (1,3) → (0,3)  = 7
        char[][] g4 = grid(
                "B#.D",
                ".#..",
                "...."
        );
        printDeskDistances("case 4 (walls)", s.deskDistances(g4));  // [(0,3,7)]

        // Case 5: desk completely walled off → -1.
        char[][] g5 = grid(
                "B#D",
                "###"
        );
        printDeskDistances("case 5 (unreachable)", s.deskDistances(g5));  // [(0,2,-1)]

        // Case 6: no bathrooms at all → every desk is -1.
        char[][] g6 = grid(
                "D.D",
                "..."
        );
        printDeskDistances("case 6 (no B)", s.deskDistances(g6));   // both -1

        // Bonus: print the full distance grid for visualization (case 3).
        System.out.println("\nfull distance grid for case 3:");
        printGrid(s.distanceGrid(g3));
    }

    private static void printDeskDistances(String label, List<int[]> entries) {
        StringBuilder sb = new StringBuilder(label).append(": ");
        for (int[] e : entries) {
            sb.append("(").append(e[0]).append(",").append(e[1])
              .append("→").append(e[2]).append(") ");
        }
        System.out.println(sb.toString().trim());
    }

    private static void printGrid(int[][] g) {
        for (int[] row : g) {
            StringBuilder sb = new StringBuilder();
            for (int v : row) {
                sb.append(String.format("%3d ", v));
            }
            System.out.println(sb);
        }
    }
}
