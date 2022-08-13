package uber.phone;

import java.util.Arrays;

public class RobotDist2 {
    static class Node {
        int top, left, right, bottom;
        public Node() {
            this.bottom = this.top = this.left = this.right = 0;
        }
    }

    public static int[] findRobotDist(char[][] board, int[] queries) {
        int rows = board.length, cols = board[0].length;
        Node[][] dp = new Node[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                dp[i][j] = new Node();
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == 'X') {
                    continue;
                }
                if (i == 0) {
                    dp[i][j].top = 1;
                } else {
                    dp[i][j].top = dp[i - 1][j].top + 1;
                }
                if (j == 0) {
                    dp[i][j].left = 1;
                } else {
                    dp[i][j].left = dp[i][j - 1].left + 1;
                }
            }
        }
        int[] result = new int[]{-1,-1};

        for (int i = rows - 1; i >= 0; i--) {
            for (int j = cols - 1; j >= 0; j--) {
                if (board[i][j] == 'X') {
                    continue;
                }
                if (i == rows - 1) {
                    dp[i][j].bottom = 1;
                } else {
                    dp[i][j].bottom = dp[i + 1][j].bottom + 1;
                }
                if (j == cols - 1) {
                    dp[i][j].right = 1;
                } else {
                    dp[i][j].right = dp[i][j + 1].right + 1;
                }
                if (dp[i][j].left == queries[0] && dp[i][j].top == queries[1]
                        && dp[i][j].bottom == queries[2] && dp[i][j].right == queries[3] ) {
                    return new int[]{i, j};
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
//        int[] query = new int[]{2,2,4,1};
        int[] query = new int[]{1,1,3,4};
        System.out.println(Arrays.toString(findRobotDist(board, query)));
    }
}
