package snowflake.mianjing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/*
Problem Description
You need to build a tool that acts like a Connect-4 board. You will work with a grid that has m rows and n columns.

An empty spot is marked as '0'.
Spots with pieces are marked with letters like R, Y, or B.

This interview has three parts:
  1. drop(color, col) — piece falls to the lowest empty row in that column. Throw if column is full.
  2. remove() — remove every 4-connected group of same-color pieces with size >= 2.
  3. settleDown() — gravity: remaining pieces fall down to fill gaps.
     drop / remove / settleDown must continue to work correctly in any order,
     so the per-column "next free row" must be re-derived after structural
     changes (remove + gravity).
 */
public class GridDropRemoveDuplicates {

    private static final char EMPTY = '0';
    private static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    int rows, cols;
    char[][] board;
    int[] height;

    public GridDropRemoveDuplicates(int m, int n) {
        this.rows = m;
        this.cols = n;
        this.board = new char[m][n];
        for (char[] row : board) Arrays.fill(row, EMPTY);
        this.height = new int[n];
    }

    public void drop(int c, char color) {
        if (c < 0 || c >= cols) {
            throw new IllegalArgumentException("col out of range: " + c);
        }
        if (height[c] == rows) {
            throw new IllegalStateException("No space left for col " + c);
        }
        int r = rows - 1 - height[c];
        board[r][c] = color;
        height[c]++;
    }

    /*
     * Remove every 4-connected group of same color whose size >= 2.
     * Singletons stay. Uses iterative BFS to avoid stack overflow on large
     * same-color regions; a `visited` array lets us decide based on group
     * size before mutating the board.
     */
    public void remove() {
        boolean[][] visited = new boolean[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (visited[r][c] || board[r][c] == EMPTY) continue;
                List<int[]> group = collectGroup(r, c, visited);
                if (group.size() >= 2) {
                    for (int[] cell : group) board[cell[0]][cell[1]] = EMPTY;
                }
            }
        }
        recomputeHeights();
    }

    private List<int[]> collectGroup(int r0, int c0, boolean[][] visited) {
        char color = board[r0][c0];
        List<int[]> group = new ArrayList<>();
        Deque<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[]{r0, c0});
        visited[r0][c0] = true;

        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            group.add(cell);
            for (int[] d : DIRS) {
                int nr = cell[0] + d[0], nc = cell[1] + d[1];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                if (visited[nr][nc] || board[nr][nc] != color) continue;
                visited[nr][nc] = true;
                queue.offer(new int[]{nr, nc});
            }
        }
        return group;
    }

    /*
     * Apply gravity: for each column, pull non-empty cells down to the bottom.
     * Two-pointer pass per column: i scans from the bottom looking for the
     * next empty slot, j scans above i for the next non-empty piece to drop in.
     */
    public void settleDown() {
        for (int c = 0; c < cols; c++) {
            int i = rows - 1;
            while (i >= 0) {
                while (i >= 0 && board[i][c] != EMPTY) i--;
                if (i < 0) break;
                int j = i - 1;
                while (j >= 0 && board[j][c] == EMPTY) j--;
                if (j < 0) break;
                board[i][c] = board[j][c];
                board[j][c] = EMPTY;
                i--;
            }
        }
        recomputeHeights();
    }

    /*
     * After any structural change (remove + gravity), the cached "next free row"
     * counter is stale. Recompute it from the board itself: assuming gravity
     * has packed every column to the bottom, height[c] is the count of
     * non-empty cells walking up from the bottom.
     */
    private void recomputeHeights() {
        for (int c = 0; c < cols; c++) {
            int h = 0;
            for (int r = rows - 1; r >= 0 && board[r][c] != EMPTY; r--) h++;
            height[c] = h;
        }
    }

    public char[][] getBoard() {
        return board;
    }

    public void print() {
        for (char[] row : board) System.out.println(new String(row));
        System.out.println();
    }

    public static void main(String[] args) {
        GridDropRemoveDuplicates g = new GridDropRemoveDuplicates(3, 3);

        g.drop(1, 'Y');
        g.drop(1, 'Y');
        g.drop(1, 'Y');
        System.out.println("After 3x drop col=1, Y:");
        g.print();
        try {
            g.drop(1, 'Y');
            System.out.println("ERROR: 4th drop should have thrown");
        } catch (IllegalStateException e) {
            System.out.println("Got expected exception: " + e.getMessage());
        }

        // R Y 0
        // B Y Y   ->  remove the Y group (size 3) but keep the singletons
        // B R 0
        GridDropRemoveDuplicates g2 = new GridDropRemoveDuplicates(3, 3);
        g2.board = new char[][]{
                {'R', 'Y', '0'},
                {'B', 'Y', 'Y'},
                {'B', 'R', '0'},
        };
        // Bs are connected vertically -> a group of size 2 (also removed).
        g2.remove();
        System.out.println("After remove():");
        g2.print();
        g2.settleDown();
        System.out.println("After settleDown():");
        g2.print();

        // Drop a piece into col 0 to verify height[] was recomputed correctly.
        g2.drop(0, 'G');
        System.out.println("After drop(0, G):");
        g2.print();

        // Singleton stays test: a board of R Y R should not change after remove().
        GridDropRemoveDuplicates g3 = new GridDropRemoveDuplicates(1, 3);
        g3.board = new char[][]{{'R', 'Y', 'R'}};
        g3.remove();
        System.out.println("Singletons should stay:");
        g3.print();
    }
}
