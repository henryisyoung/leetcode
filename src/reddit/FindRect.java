package reddit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindRect {
    public static int[] findRect(int[][] board) {
        if (board == null || board.length == 0 || board[0] == null || board[0].length == 0) {
            return new int[0];
        }
        int rows = board.length, cols = board[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == 1) {
                    int r = i, c = j;
                    while (r < rows && board[r][j] == 1) {
                        r++;
                    }
                    int height = r - i;
                    while (c < cols && board[i][c] == 1) {
                        c++;
                    }
                    int width = c - j;
                    return new int[]{i, j, height, width};
                }
            }
        }
        return new int[0];
    }
    public static List<int[]> findMultiRects(int[][] board) {
        List<int[]> result = new ArrayList<>();
        if (board == null || board.length == 0 || board[0] == null || board[0].length == 0) {
            return result;
        }
        int rows = board.length, cols = board[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == 1) {
                    int r = i, c = j;
                    while (r < rows && board[r][j] == 1) {
                        r++;
                    }
                    int height = r - i;
                    while (c < cols && board[i][c] == 1) {
                        c++;
                    }
                    int width = c - j;
                    for (int k = i; k < r; k++) {
                        for (int t = j; t < c; t++) {
                            board[k][t] = 0;
                        }
                    }
                    result.add(new int[]{i,j,height,width});
                }
            }
        }

        return result;
    }
    public static void main(String[] args) {
        int[][] board = {
                {0,1,1,0,1,0},
                {0,1,1,0,1,0},
                {0,0,0,0,0,0},
                {0,1,1,0,1,0},
                {0,1,1,0,1,0},
        };
        for (int[] point : findMultiRects(board)) {
            System.out.println(Arrays.toString(point));
        }
    }
}
