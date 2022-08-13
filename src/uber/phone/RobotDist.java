package uber.phone;

import java.util.Arrays;

public class RobotDist {

    public static int[] findRobotDist(char[][] board, int[] queries) {
        int[] result = new int[2];
        int rows = board.length, cols = board[0].length;
        int[][] leftDist = new int[rows][cols];
        int[][] rightDist = new int[rows][cols];
        int[][] topDist = new int[rows][cols];
        int[][] bottomDist = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == 'X') {
                    continue;
                }
                if (i == 0) {
                    topDist[i][j] = 1;
                } else {
                    topDist[i][j] = 1 + topDist[i - 1][j];
                }
                if (j == 0) {
                    leftDist[i][j] = 1;
                } else {
                    leftDist[i][j] = leftDist[i][j - 1] + 1;
                }
            }
        }

        for (int i = rows - 1; i >= 0; i--) {
            for (int j = cols - 1; j >= 0; j--) {
                if (board[i][j] == 'X') {
                    continue;
                }
                if (i == rows - 1) {
                    bottomDist[i][j] = 1;
                } else {
                    bottomDist[i][j] = 1 + bottomDist[i + 1][j];
                }

                if (j == cols - 1) {
                    rightDist[i][j] = 1;
                } else {
                    rightDist[i][j] = rightDist[i][j + 1] + 1;
                }
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == 'O') {
                    int l = leftDist[i][j], r = rightDist[i][j], t = topDist[i][j], b = bottomDist[i][j];
                    if (Arrays.equals(new int[]{l, t,b,r}, queries)) {
                        return new int[]{i, j};
                    }
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        char[][] board = {
                {'O', 'E', 'E', 'E', 'X'},
                {'E', 'O', 'X', 'X', 'X'},
                {'E', 'E', 'E', 'E', 'E'},
                {'X', 'E', 'O', 'E', 'E'},
                {'X', 'E', 'X', 'E', 'X'},
        };
        int[] query = new int[]{2,2,4,1};
        System.out.println(Arrays.toString(findRobotDist(board, query)));
    }
}
