package roblox.karat;

import java.util.*;

public class ZeroRowCol {
    public List<Integer> findZeroRowCol(int[][] board) {
        List<Integer> list = new ArrayList<>();
//        1. 2D matrix, 0 或 1，找出全为0的行和列
        return list;
    }

    public static int[] findNearestExit(int[][] board, int[] pos) {
        int rows = board.length, cols = board[0].length;
        Queue<int[]> queue = new LinkedList<>();
        queue.add(pos);
        if (pos[0] == rows - 1 || pos[0] == 0) return pos;
        if (pos[1] == cols - 1 || pos[1] == 0) return pos;

        boolean[][] visited = new boolean[rows][cols];
        visited[pos[0]][pos[1]] = true;
        int[][] dirs = {{0,1},{1,0},{-1,0},{0,-1}};

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] cur = queue.poll();
                for (int[] dir : dirs) {
                    int nr = cur[0] + dir[0], nc = cur[1] + dir[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !visited[nr][nc] && board[nr][nc] == 0) {
                        if (nr == 0 || nr == rows - 1 || nc == 0 || nc == cols - 1) {
                            return new int[]{nr, nc};
                        }
                        queue.add(new int[]{nr, nc});
                        visited[nr][nc] = true;
                    }
                }
            }
        }
        return new int[0];
    }

    public static void main(String[] args) {
        int[][] board = {
                {1,0,1,1},
                {1,0,1,1},
                {1,0,1,1},
                {1,0,0,0},
                {1,0,0,1},
        };
        int[] pos = {3,1};
        System.out.println(Arrays.toString(findNearestExit(board, pos)));
    }
}
