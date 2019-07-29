package dropbox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameOfLifeMillion {
    public void gameOfLifeMillionBoard(String path) throws Exception {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line1 = reader.readLine();
            String line2 = reader.readLine();
            String line3 = reader.readLine();
            String line;
            int[][] board = new int[3][1000000];
            buildArray(line1, line2, line3);
            gameOfLifeNoExtraSpace(board);
            BufferedWriter bw = new BufferedWriter(new FileWriter("result.txt"));
            while ((line = reader.readLine()) != null) {
                bw.write(convertToString(board[0]));
                board[0] = board[1];
                board[1] = board[2];
                board[2] = buildArray(line);
                gameOfLifeNoExtraSpace(board);
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public void gameOfLifeNoExtraSpace(int[][] board) {
        if (board == null || board.length == 0 || board[0] == null || board[0].length == 0) {
            return;
        }
        int rows = board.length,  cols = board[0].length;
        int[][] dirs = {{1,0},{-1,0},{-1,-1},{0,1},{0,-1},{1,-1},{1,1},{-1,1}};
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int count = 0;
                for (int[] dir : dirs) {
                    int nr = i + dir[0], nc = j + dir[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                        if (board[nr][nc] == 1 || board[nr][nc] == 2) count++;
                    }
                }
                if (board[i][j] == 1) {
                    if (count < 2 || count > 3) {
                        board[i][j] = 2;
                    }
                } else {
                    if (count == 3) {
                        board[i][j] = 3;
                    }
                }
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = board[i][j] % 2;
            }
        }
    }

    private String convertToString(int[] board) {
        return "";
    }

    private int[] buildArray(String line1, String line2, String line) {
        return null;
    }
    private int[] buildArray(String line) {
        return null;
    }


}
