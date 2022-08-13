package uber.phone.leetcode;

import java.util.LinkedList;
import java.util.Queue;

public class ShortestPathInBinaryMatrix {
    public static int shortestPathBinaryMatrix(int[][] grid) {
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0},{1,1},{-1,-1},{-1,1},{1,-1}};
        int rows = grid.length, cols = grid[0].length;
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{0, 0});
        boolean[][] visited = new boolean[rows][cols];
        visited[0][0] = true;
        int dist = 1;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0 ; i < size; i++) {
                int[] cur = queue.poll();

                if (cur[0] == rows - 1 && cur[1] == cols - 1) {
                    return dist;
                }
                int r = cur[0], c = cur[1];
                for (int[] dir : dirs) {
                    int nr = r + dir[0], nc = c + dir[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !visited[nr][nc] && grid[nr][nc] == 0) {
                        queue.add(new int[]{nr, nc});
                        visited[nr][nc] = true;
                    }
                }
            }
            dist++;
        }
        return -1;
    }

    public static void main(String[] args) {
        int[][] grid = {{0,0,0},{1,1,0},{1,1,0}};
        System.out.println(shortestPathBinaryMatrix(grid));
    }
}
