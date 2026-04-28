package snowflake.mianjing;

/*
Problem Requirements
You are provided with a game board of size m x n. Two players, "a" and "b", take turns playing. Each spot on the board contains one of three characters:

"." representing an empty space.
"a" representing a piece from Player A.
"b" representing a piece from Player B.
You will receive a specific move consisting of a row x, a column y, and the player making the move. Your task is to simulate placing that player's piece at board[x][y]. After placing the piece, determine if the player has won the game.

Rules for winning: A player wins if they connect 4 consecutive pieces in a straight line. This line can be in any of the following directions:

Horizontal (Left to Right)
Vertical (Up and Down)
Diagonal (\ direction)
Anti-diagonal (/ direction)
Important Rule: If the target spot board[x][y] is not empty (it already has a piece), the move is invalid. In this case, simply return false.

Note: The coordinates x and y are 0-indexed.

Test Cases
Case 1:

Input: board = [["a","a","a","."],[".",".",".","."]] x = 0, y = 3, player = "a"

Output: true

Why? When you place a piece at (0, 3), Player A gets four "a" pieces in a row horizontally.

Case 2:

Input: board = [["a","b",".","."],["b","a",".","."],[".",".",".","."],[".",".",".","."]] x = 2, y = 2, player = "a"

Output: false

Input Limits
The board dimensions m and n are between 1 and 200.
Each cell in board[i][j] will only contain ".", "a", or "b".
The player input is always either "a" or "b".
Row x is between 0 and m - 1.
Column y is between 0 and n - 1.
 */
public class ConnectFour {
    private static final int K = 4;

    // 4 line directions (each represented by ONE delta; we extend in both
    // (+dr,+dc) and (-dr,-dc) when counting through (x,y)).
    //   horizontal       vertical          diagonal '\'      anti-diag '/'
    private static final int[][] DIRS = {
            {0, 1},          {1, 0},          {1, 1},           {1, -1}
    };

    /**
     * Place `player`'s piece at (x, y) and return true iff this move wins.
     * If the cell is already occupied, the move is invalid → return false
     * and do NOT change the board.
     */
    public boolean play(char[][] board, int x, int y, char player) {
        if (board == null || board.length == 0) return false;
        int m = board.length, n = board[0].length;
        if (x < 0 || x >= m || y < 0 || y >= n) return false;
        if (player != 'a' && player != 'b') return false;
        if (board[x][y] != '.') return false;

        board[x][y] = player;

        for (int[] d : DIRS) {
            // Count pieces on this line passing through (x, y), inclusive.
            int run = 1
                    + countRun(board, x, y,  d[0],  d[1], player)
                    + countRun(board, x, y, -d[0], -d[1], player);
            if (run >= K) return true;
        }
        return false;
    }

    // Walk from (x,y) in direction (dr,dc) WITHOUT counting (x,y) itself.
    private int countRun(char[][] board, int x, int y, int dr, int dc, char player) {
        int m = board.length, n = board[0].length;
        int r = x + dr, c = y + dc, count = 0;
        while (r >= 0 && r < m && c >= 0 && c < n && board[r][c] == player) {
            count++;
            r += dr;
            c += dc;
        }
        return count;
    }

    // Helper for tests: convert String[] (each row is a String) → char[][].
    private static char[][] toBoard(String... rows) {
        char[][] b = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) b[i] = rows[i].toCharArray();
        return b;
    }

    public static void main(String[] args) {
        ConnectFour s = new ConnectFour();

        // Case 1: horizontal win on row 0
        char[][] b1 = toBoard(
                "aaa.",
                "...."
        );
        System.out.println(s.play(b1, 0, 3, 'a')); // true

        // Case 2: scattered pieces, no win
        char[][] b2 = toBoard(
                "ab..",
                "ba..",
                "....",
                "...."
        );
        System.out.println(s.play(b2, 2, 2, 'a')); // false

        // Vertical win: 3 a's stacked, drop a 4th on top
        char[][] b3 = toBoard(
                "....",
                "a...",
                "a...",
                "a..."
        );
        System.out.println(s.play(b3, 0, 0, 'a')); // true

        // Diagonal '\' win
        char[][] b4 = toBoard(
                "a...",
                ".a..",
                "..a.",
                "...."
        );
        System.out.println(s.play(b4, 3, 3, 'a')); // true

        // Anti-diagonal '/' win, placing the middle piece (not the end)
        char[][] b5 = toBoard(
                "...a",
                "....",   // (1,2) will become 'a'
                ".a..",
                "a..."
        );
        System.out.println(s.play(b5, 1, 2, 'a')); // true

        // Cell already occupied → invalid move
        char[][] b6 = toBoard(
                "a...",
                "...."
        );
        System.out.println(s.play(b6, 0, 0, 'a')); // false

        // Mixed pieces on the line — should NOT count as a run for 'a'
        char[][] b7 = toBoard(
                "aab.",
                "...."
        );
        System.out.println(s.play(b7, 0, 3, 'a')); // false (only 2 a's then b)
    }
}
