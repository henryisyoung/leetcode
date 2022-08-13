package databricks;

import java.util.LinkedList;
import java.util.Queue;

public class FastestCommute {
    // https://www.1point3acres.com/bbs/thread-887099-1-1.html
    // https://www.1point3acres.com/bbs/thread-894516-1-1.html
    /*
    给定一个2D grid，里面含有‘X', 'S', 'D', '1', '2', '3',
    分别代表路障，start, destination, 走路，汽车，开车可以走的路，
    额外的input是不同交通工具每走一格的time cost和money cost, 现在我们要从S出发到D，
    希望可以找到一条时间最快的路，如果有多种交通工具可以达成相同的时间，选择花钱更少的那个。

    没在面经里见过，也可能是我没看到，给一个2d array里面有不同的char，x代表block，1，2。。之类的数字代表不同的通勤方式，
    每种会需要不同的时间和开销，问从起点到终点的最快的通勤方式是什么，如果有相同的返回开销最少的。下面是个栗子。
    输入：
    2D Grid:
    |3|3|S|2|X|X|
    |3|1|1|2|X|2|
    |3|1|1|2|2|2|
    |3|1|1|1|D|3|
    |3|3|3|3|3|4|
    |4|4|4|4|4|4|
    时间array: [3, 2, 1, 1]  表示 (通勤方式) 1, 2, 3, 4 -> （时间）3, 2, 1, 1
    开销array: [0, 1, 3, 2]  表示 (通勤方式) 1, 2, 3, 4 -> （开销）0, 1, 3, 2
    输出：2 （用2可以最快的从S到D）
     */

    public static char findBestCommute(int[] timeCost, int[] moneyCost, char[][] board) {
        int rows = board.length, cols = board[0].length;
        int minTime = Integer.MAX_VALUE, minMoney = Integer.MAX_VALUE;
        char result = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == 'S') {
                    for (int w = 0; w < timeCost.length; w++) {
                        int time = timeCost[w], money = moneyCost[w];
                        char way = (char) (w + '1');
                        int level = bfsFindPath(i, j, board, way);
                        if (level == -1) {
                            continue;
                        }
                        int[] cost = new int[]{time * level, money * level};
                        if (cost[0] < minTime) {
                            minTime = cost[0];
                            result = way;
                        } else if (cost[0] == minTime && minMoney > cost[1]) {
                            minTime = cost[0];
                            minMoney = cost[1];
                            result = way;
                        }
                    }
                }
            }
        }

        return result;
    }

    private static int bfsFindPath(int r, int c, char[][] board, char way) {
        int rows = board.length, cols = board[0].length;
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[rows][cols];
        visited[r][c] = true;
        queue.add(new int[]{r , c});
        int level = 0;
        int[][] dirs = {{1,0}, {0,1},{0,-1},{-1,0}};

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                int[] cur = queue.poll();
                if (board[cur[0]][cur[1]] == 'D') {
                    return level;
                }
                for (int[] dir : dirs) {
                    int nr = dir[0] + cur[0], nc = dir[1] + cur[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !visited[nr][nc] && (board[nr][nc] == way || board[nr][nc] == 'D' )) {
                        visited[nr][nc] = true;
                        queue.add(new int[]{nr, nc});
                    }
                }
            }
            level++;
        }

        return -1;
    }

    public static void main(String[] args) {
        char[][] board = {
                {'3','3','S','2','X','X'},
                {'3','1','1','2','X','2'},
                {'3','1','1','2','2','2'},
                {'3','1','1','1','D','3'},
                {'3','3','3','3','3','4'},
                {'4','4','4','4','4','4'},
        };
        int[] timeCost = {3, 2, 1, 1};
        int[] moneyCost = {0, 1, 3, 2};
        System.out.println(findBestCommute(timeCost, moneyCost, board));
    }
}
