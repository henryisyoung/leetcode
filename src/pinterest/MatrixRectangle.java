package pinterest;

import java.util.*;

public class MatrixRectangle {
    public List<int[]> findAllRect(int[][] board) {
        int rows = board.length, cols = board[0].length;
        List<int[]> result = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == 0) {
                    int row = 0, col = 0;
                    while (i + row < rows && board[i + row][j] == 0) {
                        row++;
                    }
                    while (j + col < cols && board[i][j + col] == 0) {
                        col++;
                    }
                    int[] arr = new int[]{i,j,i + row - 1, j + col - 1};
                    result.add(arr);
                    for (int r = i; r < i + row; r++) {
                        for (int c = j; c < j + col; c++) {
                            board[r][c] = 1;
                        }
                    }
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[][] matrix = new int[][]{
                {1,1,1,1,1,1},
                {0,0,1,0,1,1},
                {0,0,1,0,1,0},
                {1,1,1,0,1,0},
                {1,0,0,1,1,1}
        };

//        [ [1,0,2,1], [1,3,3,3], [2,5,3,5], [4,1,4,2] ] 
        MatrixRectangle solver = new MatrixRectangle();
        List<int[]> result = solver.findAllRect(matrix);
        for (int[] arr : result) {
            System.out.println(Arrays.toString(arr));
        }
    }
}
