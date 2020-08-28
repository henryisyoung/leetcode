package apple;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class TheMaze {
    public boolean hasPath(int[][] maze, int[] start, int[] destination) {
        if (maze == null || maze.length == 0 || maze[0] == null || maze[0].length == 0) return false;
        Queue<int[]> queue = new LinkedList<>();
        queue.add(start);
        int rows = maze.length, cols = maze[0].length;

        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        boolean[][] visited = new boolean[start[0]][start[1]];

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1];
            if (Arrays.equals(cur, destination)) return true;
            for (int[] dir : dirs) {
                int nr = r + dir[0], nc = c + dir[1];
                while (nr >= 0 && nr < rows && nc >= 0 && nc < cols && maze[nr][nc] == 0) {
                    nr += dir[0];
                    nc += dir[1];
                }
                nr -= dir[0];
                nc -= dir[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && maze[nr][nc] == 0 && !visited[nr][nc]) {
                    queue.add(new int[]{nr, nc});
                    visited[nr][nc] = true;
                }
            }
        }
        return false;
    }
}
