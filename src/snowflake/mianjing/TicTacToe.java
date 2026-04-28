package snowflake.mianjing;
/*
The Challenge
You are given a Tic-Tac-Toe board (represented by the variable board). You need to check if the board shows a game state that is actually possible.

Return true if the board layout could happen in a real game played by the rules. If the layout is impossible, return false.

The board is a 3x3 grid. Each box in the grid contains an "X", an "O", or a space " " (which means it is empty).

Game Rules
To determine if a board is valid, you must follow these rules:

Players take turns. They place their mark in an empty box.
The player using X always goes first.
A player can only place one mark per turn.
The game ends immediately if a player wins or if the board is full.
Sample Cases
Case 1:

Input: board = ["O "," "," "]

Output: false

Case 2:

Input: board = ["XOX","O O","XOX"]

Output: true

Input Constraints
board.length == 3 (The board always has 3 rows)
board[i].length == 3 (The board always has 3 columns)
board[i][j] is always one of these three characters: 'X', 'O', or ' '.
 */
/*
 * Validity rules derived from the game:
 *   1. X always moves first, so #X must equal #O (O just moved) or #X == #O + 1 (X just moved).
 *   2. The game stops the instant somebody wins, so:
 *        - If X has a winning line, X must have just moved => #X == #O + 1.
 *        - If O has a winning line, O must have just moved => #X == #O.
 *        - X and O cannot both have winning lines (the loser would have moved after the game ended).
 *
 * O(1) time and space — board is fixed at 3x3.
 */
public class TicTacToe {
    public boolean validTicTacToe(String[] board) {
        int xCount = 0, oCount = 0;
        for (String row : board) {
            for (int j = 0; j < 3; j++) {
                char c = row.charAt(j);
                if (c == 'X') xCount++;
                else if (c == 'O') oCount++;
            }
        }

        if (xCount != oCount && xCount != oCount + 1) return false;

        boolean xWins = wins(board, 'X');
        boolean oWins = wins(board, 'O');

        if (xWins && oWins) return false;
        if (xWins && xCount != oCount + 1) return false;
        if (oWins && xCount != oCount) return false;

        return true;
    }

    private boolean wins(String[] board, char p) {
        for (int i = 0; i < 3; i++) {
            if (board[i].charAt(0) == p && board[i].charAt(1) == p && board[i].charAt(2) == p) return true;
            if (board[0].charAt(i) == p && board[1].charAt(i) == p && board[2].charAt(i) == p) return true;
        }
        if (board[0].charAt(0) == p && board[1].charAt(1) == p && board[2].charAt(2) == p) return true;
        if (board[0].charAt(2) == p && board[1].charAt(1) == p && board[2].charAt(0) == p) return true;
        return false;
    }

    public static void main(String[] args) {
        TicTacToe s = new TicTacToe();

        // Provided cases.
        System.out.println(s.validTicTacToe(new String[]{"O  ", "   ", "   "})); // false (O moved first)
        System.out.println(s.validTicTacToe(new String[]{"XOX", "O O", "XOX"})); // true

        // Edge cases.
        System.out.println(s.validTicTacToe(new String[]{"   ", "   ", "   "})); // true (empty)
        System.out.println(s.validTicTacToe(new String[]{"XXX", "   ", "OOO"})); // false (both win)
        System.out.println(s.validTicTacToe(new String[]{"XOX", " X ", "O O"})); // false (X wins but counts off: X=3,O=2 => valid? check)
        System.out.println(s.validTicTacToe(new String[]{"XXX", "OOO", "X  "})); // false (both win)
        System.out.println(s.validTicTacToe(new String[]{"XOX", "OXO", "XOX"})); // true (X diag wins, X=5,O=4)
    }
}
