package uber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class TicTacToe {
    int[] rows, cols;
    int diag, antiDiag, n;
    public TicTacToe(int n) {
        this.rows = new int[n];
        this.cols = new int[n];
        this.antiDiag = 0;
        this.diag = 0;
        this.n = n;
    }

    public int move(int row, int col, int player) {
        int val = player == 1 ? 1 : -1;
        rows[row] += val;
        cols[col] += val;
        if (row == col) diag += val;
        if (row + col == n - 1) antiDiag += val;
        if (Math.abs(rows[row]) == n || Math.abs(cols[col]) == n || Math.abs(antiDiag) == n || Math.abs(diag) == n) {
            return player;
        }
        return 0;
    }

    public static void main(String[] args) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Read: " + line);
            }
        }
    }
}
