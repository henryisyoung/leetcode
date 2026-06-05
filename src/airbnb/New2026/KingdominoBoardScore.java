package airbnb.New2026;

import java.util.ArrayDeque;
import java.util.Deque;

/*
Board Score — Connected Areas × Crowns  (Airbnb, inspired by Kingdomino).

Given an R×C board of 2-char tiles like "G2" (area_type = 'G', crowns = 2),
score the board. A region is a maximal 4-connected component of cells sharing
the same area_type, and contributes:

    region_score = (#cells in region) × (sum of crowns in region)

Return the sum over all regions.

Example
  [["G1","G2","W0","W1","S1"],
   ["G2","G3","W0","W1","S1"],
   ["S2","S3","S1","G1","S1"],
   ["G1","G2","W0","W1","S1"],
   ["G1","G2","W0","W1","S1"]]
  -> 116

The trap: it's cells × crowns, NOT cells + crowns. Re-read before coding.

Algorithm — BFS flood fill
  Iterate every cell. If unvisited, BFS its same-type component, summing
  cell count and crown count, then add cells × crowns to the total. Each
  cell is visited once.

  Time:   O(R · C)
  Memory: O(R · C)  (visited grid + BFS queue)
*/
public class KingdominoBoardScore {

    private static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public static int boardScore(String[][] board) {
        if (board == null || board.length == 0 || board[0].length == 0) return 0;
        int R = board.length, C = board[0].length;
        boolean[][] visited = new boolean[R][C];
        int total = 0;

        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                if (visited[r][c]) continue;
                total += scoreRegion(board, visited, r, c, R, C);
            }
        }
        return total;
    }

    /** BFS the same-area_type component containing (sr, sc); return cells × crowns. */
    private static int scoreRegion(String[][] board, boolean[][] visited,
                                   int sr, int sc, int R, int C) {
        char type = board[sr][sc].charAt(0);
        Deque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{sr, sc});
        visited[sr][sc] = true;

        int cells = 0, crowns = 0;
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1];
            cells++;
            crowns += crownsOf(board[r][c]);
            for (int[] d : DIRS) {
                int nr = r + d[0], nc = c + d[1];
                if (nr < 0 || nr >= R || nc < 0 || nc >= C) continue;
                if (visited[nr][nc]) continue;
                if (board[nr][nc].charAt(0) != type) continue;
                visited[nr][nc] = true;
                queue.add(new int[]{nr, nc});
            }
        }
        return cells * crowns;
    }

    /** Crowns = everything after the area-type char (handles multi-digit counts). */
    private static int crownsOf(String tile) {
        return Integer.parseInt(tile.substring(1));
    }

    /* --------------------------- tests --------------------------- */

    public static void main(String[] args) {
        // Spec example -> 116 (hand-verified region by region).
        check("spec", new String[][]{
                {"G1", "G2", "W0", "W1", "S1"},
                {"G2", "G3", "W0", "W1", "S1"},
                {"S2", "S3", "S1", "G1", "S1"},
                {"G1", "G2", "W0", "W1", "S1"},
                {"G1", "G2", "W0", "W1", "S1"}}, 116);

        // Single cell.
        check("single cell", new String[][]{{"G3"}}, 3);          // 1 × 3

        // Single cell, zero crowns.
        check("single zero crown", new String[][]{{"W0"}}, 0);    // 1 × 0

        // Whole board one region.
        check("one region all same", new String[][]{
                {"G1", "G1"},
                {"G1", "G1"}}, 16);                                // 4 cells × 4 crowns

        // Two separate regions of the same type, split by another type.
        check("split same type", new String[][]{
                {"G1", "S0", "G1"}}, 2);                           // two G regions: 1×1 + 1×1; S: 1×0

        // Diagonal-only touch does NOT connect (4-connectivity).
        check("diagonal not connected", new String[][]{
                {"G1", "W0"},
                {"W0", "G1"}}, 2);                                 // two G singletons: 1+1; W: two singletons ×0

        // All zero crowns -> total 0 regardless of sizes.
        check("all zero crowns", new String[][]{
                {"G0", "G0"},
                {"S0", "S0"}}, 0);

        // Multi-digit crowns (clarified edge case).
        check("multi-digit crowns", new String[][]{{"G10", "G2"}}, 24);  // 2 cells × (10+2)=12

        // Empty board.
        check("empty", new String[][]{}, 0);
    }

    private static void check(String label, String[][] board, int expected) {
        int got = boardScore(board);
        System.out.println((got == expected ? "OK   " : "FAIL ")
                + label + " expected=" + expected + " got=" + got);
    }
}
