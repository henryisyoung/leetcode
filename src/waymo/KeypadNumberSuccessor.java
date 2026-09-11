package waymo;

/*
Keypad Number Successor

Keypad:
  1  2  3
  4  5  6
  7  8  9
 -1  0 -1

Given a number length, generate all valid numbers by walking from digit to
neighbor digit. A cell with -1 is invalid. The direction order is fixed, so the
generated sequence is deterministic.

This file uses direction order:
  left, down, right, up

Example for len = 2:
  starting at 1 -> 14, 12
  starting at 2 -> 21, 25, 23

So after 21, the next generated number is 25.

Notes:
  - The first digit can be any valid keypad digit, scanned row-major.
  - For a version restricted to one starting digit, use generateFromStart().
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeypadNumberSuccessor {

    private static final int[][] BOARD = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9},
            {-1, 0, -1}
    };

    // Fixed order: left, down, right, up.
    private static final int[][] DIRS = {
            {0, -1},
            {1, 0},
            {0, 1},
            {-1, 0}
    };

    private final Map<Integer, int[]> position = new HashMap<>();

    public KeypadNumberSuccessor() {
        for (int r = 0; r < BOARD.length; r++) {
            for (int c = 0; c < BOARD[0].length; c++) {
                if (BOARD[r][c] != -1) {
                    position.put(BOARD[r][c], new int[]{r, c});
                }
            }
        }
    }

    /** Generate all valid numbers of length len, starting digit scanned row-major. */
    public List<String> generateAll(int len) {
        List<String> result = new ArrayList<>();
        if (len <= 0) return result;

        for (int r = 0; r < BOARD.length; r++) {
            for (int c = 0; c < BOARD[0].length; c++) {
                if (BOARD[r][c] != -1) {
                    dfs(r, c, len, String.valueOf(BOARD[r][c]), result);
                }
            }
        }
        return result;
    }

    /** Generate all valid numbers of length len that start from startDigit. */
    public List<String> generateFromStart(int startDigit, int len) {
        List<String> result = new ArrayList<>();
        if (len <= 0 || !position.containsKey(startDigit)) return result;

        int[] pos = position.get(startDigit);
        dfs(pos[0], pos[1], len, String.valueOf(startDigit), result);
        return result;
    }

    /**
     * Return the next number after current among all generated length-len
     * numbers. Returns null if current is invalid or already last.
     */
    public String nextAfter(String current, int len) {
        if (!isValidPath(current, len)) return null;

        String nextSameStart = nextWithinSameStart(current, len);
        if (nextSameStart != null) return nextSameStart;

        int startDigit = current.charAt(0) - '0';
        List<Integer> starts = orderedStartDigits();
        int startIndex = starts.indexOf(startDigit);
        if (startIndex < 0 || startIndex + 1 >= starts.size()) return null;

        return firstPathFrom(starts.get(startIndex + 1), len);
    }

    /**
     * Return the next number after current among numbers generated from one
     * starting digit only.
     */
    public String nextAfterFromStart(int startDigit, String current, int len) {
        if (!isValidPath(current, len)) return null;
        if (current.charAt(0) - '0' != startDigit) return null;
        return nextWithinSameStart(current, len);
    }

    private String nextWithinSameStart(String current, int len) {
        StringBuilder prefix = new StringBuilder(current);

        for (int i = len - 1; i >= 1; i--) {
            int parent = current.charAt(i - 1) - '0';
            int child = current.charAt(i) - '0';
            List<Integer> nextDigits = nextDigits(parent);
            int childIndex = nextDigits.indexOf(child);
            if (childIndex < 0) return null;

            if (childIndex + 1 < nextDigits.size()) {
                prefix.setLength(i);
                prefix.append(nextDigits.get(childIndex + 1));
                return appendFirstChildren(prefix, len);
            }
        }
        return null;
    }

    private String firstPathFrom(int startDigit, int len) {
        StringBuilder path = new StringBuilder();
        path.append(startDigit);
        return appendFirstChildren(path, len);
    }

    private String appendFirstChildren(StringBuilder path, int len) {
        while (path.length() < len) {
            int last = path.charAt(path.length() - 1) - '0';
            List<Integer> nextDigits = nextDigits(last);
            if (nextDigits.isEmpty()) return null;
            path.append(nextDigits.get(0));
        }
        return path.toString();
    }

    private boolean isValidPath(String current, int len) {
        if (current == null || current.length() != len || len <= 0) return false;
        for (int i = 0; i < current.length(); i++) {
            char ch = current.charAt(i);
            if (ch < '0' || ch > '9') return false;
            int digit = ch - '0';
            if (!position.containsKey(digit)) return false;
            if (i > 0 && !nextDigits(current.charAt(i - 1) - '0').contains(digit)) return false;
        }
        return true;
    }

    private List<Integer> orderedStartDigits() {
        List<Integer> starts = new ArrayList<>();
        for (int r = 0; r < BOARD.length; r++) {
            for (int c = 0; c < BOARD[0].length; c++) {
                if (BOARD[r][c] != -1) starts.add(BOARD[r][c]);
            }
        }
        return starts;
    }

    private List<Integer> nextDigits(int digit) {
        List<Integer> result = new ArrayList<>();
        int[] pos = position.get(digit);
        if (pos == null) return result;

        for (int[] dir : DIRS) {
            int nr = pos[0] + dir[0];
            int nc = pos[1] + dir[1];
            if (isValid(nr, nc)) {
                result.add(BOARD[nr][nc]);
            }
        }
        return result;
    }

    private void dfs(int r, int c, int len, String path, List<String> result) {
        if (path.length() == len) {
            result.add(path);
            return;
        }

        for (int[] dir : DIRS) {
            int nr = r + dir[0];
            int nc = c + dir[1];
            if (isValid(nr, nc)) {
                dfs(nr, nc, len, path + BOARD[nr][nc], result);
            }
        }
    }

    private boolean isValid(int r, int c) {
        return r >= 0 && r < BOARD.length
                && c >= 0 && c < BOARD[0].length
                && BOARD[r][c] != -1;
    }

    public static void main(String[] args) {
        KeypadNumberSuccessor solver = new KeypadNumberSuccessor();

        System.out.println("start=1 len=2: " + solver.generateFromStart(1, 2)); // [14, 12]
        System.out.println("start=2 len=2: " + solver.generateFromStart(2, 2)); // [21, 25, 23]
        System.out.println("after 21, len=2: " + solver.nextAfter("21", 2));    // 25

        check("next after 21", solver.nextAfter("21", 2), "25");
        check("next after 25", solver.nextAfter("25", 2), "23");
        check("next after 14 from start 1", solver.nextAfterFromStart(1, "14", 2), "12");
        check("21 is not in start 1 list", solver.nextAfterFromStart(1, "21", 2), null);

        System.out.println("All tests passed.");
    }

    private static void check(String label, String actual, String expected) {
        boolean ok = expected == null ? actual == null : expected.equals(actual);
        if (!ok) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
        System.out.println(label + ": " + actual);
    }
}
