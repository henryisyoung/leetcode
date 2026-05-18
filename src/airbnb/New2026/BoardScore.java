package airbnb.New2026;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
================================================================================
  Calculate Board Score (Airbnb)
================================================================================

  Given an R×C board, score each cell by the length of the contiguous run of
  the same character starting at that cell, going RIGHT and going DOWN.
  Total score = sum of (rightRun + downRun) over all cells.

  rightRun[i][j] = 1 + rightRun[i][j+1] if board[i][j] == board[i][j+1]
                 = 1 otherwise
  downRun[i][j]  = 1 + downRun[i+1][j] if board[i][j] == board[i+1][j]
                 = 1 otherwise

  Each cell contributes (rightRun[i][j] + downRun[i][j]) to the total.

  Complexity
    Time:   O(R · C)  — one right-to-left pass per row + one bottom-up pass per col
    Memory: O(1) extra (we accumulate into a running `score` directly,
            re-using a single per-pass int).

  ⚠ NOTE on the prompt's test cases
  ────────────────────────────────────────────────────────────────────────
  The natural reading of the problem ("starting from that cell, ... length of
  the contiguous run") produces the values below for the prompt's tests:

    Test          natural-reading score     prompt's "expected"
    Test 1 (AA/AA)         12                       12 ✓
    Test 2 (ABBB)          11                        8  ✗
    Test 3 (3x3 distinct)  18                       18 ✓
    Test 4 (AABBA/.../CCDDD)  43                    52 ✗
    Test 5 (.../..X)       18                       20 ✗

  Tests 1 & 3 match the natural reading. Tests 2, 4, 5 do NOT — they imply
  some other rule (possibly counting whole-run lengths rather than
  per-starting-cell, or including some 2D-block contribution).

  Since the *official spec example* shown in the prompt's main body is
  Test 1 (which matches), I'm implementing the natural reading. In a real
  interview I'd flag this inconsistency to the interviewer and ask them to
  re-state the rule with a counterexample.
================================================================================
*/
public class BoardScore {

    public static long boardScore(char[][] board) {
        int R = board.length, C = board[0].length;
        long score = 0;

        // Right runs: for each row, scan right-to-left so each cell knows the
        // length of the run it heads.
        for (int i = 0; i < R; i++) {
            int run = 1;              // (i, C-1) always heads a run of length 1
            score += run;
            for (int j = C - 2; j >= 0; j--) {
                run = (board[i][j] == board[i][j + 1]) ? run + 1 : 1;
                score += run;
            }
        }

        // Down runs: for each col, scan bottom-to-top.
        for (int j = 0; j < C; j++) {
            int run = 1;              // (R-1, j) always heads a run of length 1
            score += run;
            for (int i = R - 2; i >= 0; i--) {
                run = (board[i][j] == board[i + 1][j]) ? run + 1 : 1;
                score += run;
            }
        }
        return score;
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
        try { return System.in.available() > 0; } catch (IOException e) { return false; }
    }

    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] firstLine = br.readLine().trim().split("\\s+");
        int R = Integer.parseInt(firstLine[0]);
        int C = Integer.parseInt(firstLine[1]);
        char[][] board = new char[R][C];
        for (int i = 0; i < R; i++) board[i] = br.readLine().toCharArray();
        System.out.println(boardScore(board));
    }

    /* --------------------------- Demos --------------------------- */

    private static void runDemos() {
        check("AA|AA",                     12);   // Test 1 ✓
        check("ABBB",                       8);   // Test 2 (prompt says 8; natural reading → 11)
        check("ABC|DEF|GHI",               18);   // Test 3 ✓
        check("AABBA|AABBA|CCDDD",         52);   // Test 4 (prompt says 52; natural reading → 43)
        check("...|..X",                   20);   // Test 5 (prompt says 20; natural reading → 18)

        // Sanity check that should always match: single-cell board
        check("A",                          2);   // 1 (right) + 1 (down)
    }

    private static void check(String boardSpec, long expected) {
        String[] rows = boardSpec.split("\\|");
        char[][] board = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) board[i] = rows[i].toCharArray();
        long got = boardScore(board);
        System.out.println((got == expected ? "OK   " : "FAIL ")
                + "board=" + boardSpec + " expected=" + expected + " got=" + got);
    }
}
