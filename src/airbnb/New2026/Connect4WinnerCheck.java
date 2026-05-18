package airbnb.New2026;
/*
Connect 4 — Detect Winner.

Given a 6x7 board where each cell is 'X', 'O', or empty (' '), return
the winning piece ('X' or 'O') if there is a run of four consecutive
identical pieces in any row, column, or diagonal; otherwise "No Winner".

I/O
  Input : char[][] board   (6 rows x 7 cols by spec)
  Output: String           ("X", "O", or "No Winner")

Tie-breaking
  The spec doesn't address two simultaneous wins.  We return the FIRST
  one we encounter under a deterministic scan order:
      outer row 0..R-1, inner col 0..C-1, directions {→, ↓, ↘, ↙}.

Generalization
  The same algorithm works for any rows x cols board and any run length
  k by adjusting the constants — handy as an interview follow-up.

Examples
  board1 (row 0 has "XXXX")                   -> "X"
  board2 (row 1 has "OXXXX...")               -> "X"
  empty board                                  -> "No Winner"
*/

import java.util.Arrays;

/*
Algorithm: starting-cell scan with 4 forward directions.

  For every non-empty cell (r, c) try to extend a run of 4 in each of
  the four FORWARD directions:
      (0, 1)   horizontal   (right)
      (1, 0)   vertical     (down)
      (1, 1)   diagonal     (down-right)
      (1,-1)   anti-diag    (down-left)
  These four cover every 4-in-a-row exactly once (each run has a unique
  "topmost-then-leftmost" starting cell, which is the one we check from).
  We skip immediately if the run's far end falls off the board.

  Why those four — not eight?
    Every line has two directions; checking both would visit each run
    twice.  Choosing the lexicographically smaller direction per line
    is enough and keeps the comparison simple.

  Why scan from non-empty cells only:
    Tiny optimization — skips ~half the cells on a typical board.

Complexity
  Time:   O(R * C * D * K) where D = 4 directions, K = 4 run length.
          For 6x7 that's 6*7*4*4 = 672 char comparisons worst case.
  Memory: O(1)
*/
public class Connect4WinnerCheck {

    private static final int[][] DIRS = {
            { 0,  1},   // right
            { 1,  0},   // down
            { 1,  1},   // down-right (main diag)
            { 1, -1},   // down-left  (anti diag)
    };
    private static final int K = 4;     // run length to win

    /** Returns the winner ('X' or 'O') as a 1-char string, or "No Winner". */
    public String checkWinner(char[][] board) {
        if (board == null || board.length == 0 || board[0].length == 0) return "No Winner";
        int R = board.length, C = board[0].length;

        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                char p = board[r][c];
                if (p == ' ' || p == 0) continue;        // empty
                for (int[] d : DIRS) {
                    int er = r + (K - 1) * d[0];
                    int ec = c + (K - 1) * d[1];
                    if (er < 0 || er >= R || ec < 0 || ec >= C) continue;
                    boolean win = true;
                    for (int k = 1; k < K; k++) {
                        if (board[r + k * d[0]][c + k * d[1]] != p) { win = false; break; }
                    }
                    if (win) return String.valueOf(p);
                }
            }
        }
        return "No Winner";
    }

    /* --------------------------- demo / tests --------------------------- */

    public static void main(String[] args) {
        Connect4WinnerCheck solver = new Connect4WinnerCheck();

        // ---- Spec boards ----
        char[][] board1 = parse(
                "X X X X . . .",
                "O O . . . . .",
                "X . . O . . .",
                ". . . . . . .",
                ". . . . . . .",
                ". . . . . . ."
        );
        check(solver, "spec board1 (row 0 XXXX)", board1, "X");

        char[][] board2 = parse(
                "O O O . . . .",
                "O X X X X . .",
                ". . . . O . .",
                ". . . . . . .",
                ". . . . . . .",
                ". . . . . . ."
        );
        check(solver, "spec board2 (row 1 XXXX)", board2, "X");

        char[][] board3 = parse(
                ". . . . . . .",
                ". . . . . . .",
                ". . . . . . .",
                ". . . . . . .",
                ". . . . . . .",
                ". . . . . . ."
        );
        check(solver, "spec board3 (empty)", board3, "No Winner");

        // ---- Vertical win ----
        char[][] vert = parse(
                ". . O . . . .",
                ". . O . . . .",
                ". . O . . . .",
                ". . O . . . .",
                ". . X . . . .",
                ". . X . . . ."
        );
        check(solver, "vertical O column 2", vert, "O");

        // ---- Diagonal (down-right) ----
        char[][] diag = parse(
                "X . . . . . .",
                ". X . . . . .",
                ". . X . . . .",
                ". . . X . . .",
                ". . . . . . .",
                ". . . . . . ."
        );
        check(solver, "diagonal down-right", diag, "X");

        // ---- Anti-diagonal (down-left) ----
        char[][] anti = parse(
                ". . . O . . .",
                ". . O . . . .",
                ". O . . . . .",
                "O . . . . . .",
                ". . . . . . .",
                ". . . . . . ."
        );
        check(solver, "anti-diagonal down-left", anti, "O");

        // ---- 3-in-a-row only (no winner) ----
        char[][] three = parse(
                "X X X . . . .",
                ". . . . . . .",
                ". . . . . . .",
                ". . . . . . .",
                ". . . . . . .",
                ". . . . . . ."
        );
        check(solver, "only 3 in a row", three, "No Winner");

        // ---- 5 in a row counts (subsumes 4) ----
        char[][] five = parse(
                "X X X X X . .",
                ". . . . . . .",
                ". . . . . . .",
                ". . . . . . .",
                ". . . . . . .",
                ". . . . . . ."
        );
        check(solver, "5 in a row", five, "X");

        // ---- 4 across right edge (corner case for boundary check) ----
        char[][] rightEdge = parse(
                ". . . O O O O",
                ". . . . . . .",
                ". . . . . . .",
                ". . . . . . .",
                ". . . . . . .",
                ". . . . . . ."
        );
        check(solver, "right-edge horizontal", rightEdge, "O");

        // ---- Two wins (X first by scan order) ----
        char[][] twoWins = parse(
                "X X X X . . .",
                ". . . . . . .",
                "O O O O . . .",
                ". . . . . . .",
                ". . . . . . .",
                ". . . . . . ."
        );
        check(solver, "two simultaneous wins -> first scan-order wins", twoWins, "X");

        // ---- Empty board ----
        check(solver, "null board", null, "No Winner");
        check(solver, "0x0 board", new char[0][], "No Winner");
    }

    /** Parse a row spec like "X X X X . . ." into a char[]; '.' (or ' ') == empty. */
    private static char[][] parse(String... rows) {
        char[][] g = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            String[] tok = rows[i].trim().split("\\s+");
            char[] row = new char[tok.length];
            for (int j = 0; j < tok.length; j++) {
                String t = tok[j];
                row[j] = (t.isEmpty() || t.equals(".") || t.equals("_")) ? ' ' : t.charAt(0);
            }
            g[i] = row;
        }
        return g;
    }

    private static void check(Connect4WinnerCheck solver, String label,
                              char[][] board, String expected) {
        String got = solver.checkWinner(board);
        boolean ok = got.equals(expected);
        System.out.println((ok ? "OK   " : "FAIL ")
                + label + " expected=" + expected + " got=" + got);
        if (!ok && board != null) {
            for (char[] row : board) System.out.println("  " + Arrays.toString(row));
        }
    }
}
