package leetcode.graphAndSearch;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class TheMaze {
    public static boolean hasPath(int[][] maze, int[] start, int[] destination) {
        if (maze == null || maze.length == 0 || maze[0] == null || maze[0].length == 0) {
            return false;
        }
        int rows = maze.length, cols = maze[0].length;
        int[][] dp = new int[rows][cols];
        return dfsSearchAll(maze, start[0], start[1], destination[0], destination[1], dp);
    }

    private static boolean dfsSearchAll(int[][] maze, int r, int c, int er, int ec, int[][] dp) {
        if (r == er && c == ec) return true;
        if (dp[r][c] != 0) {
            return dp[r][c] == 1;
        }
        boolean result = false;
        maze[r][c] = -1;
        int rows = maze.length, cols = maze[0].length;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] dir : dirs) {
            int nr = r + dir[0], nc = c + dir[1];
            while (nr >= 0 && nr < rows && nc >= 0 && nc < cols && maze[nr][nc] != 1) {
                nr = nr + dir[0];
                nc = nc + dir[1];
            }
            nr = nr - dir[0];
            nc = nc - dir[1];
            if (maze[nr][nc] != -1) {
                result |= dfsSearchAll(maze, nr, nc, er, ec, dp);
            }
        }
        dp[r][c] = result ? 1 : -1;
        return result;
    }

    public static boolean hasPathBFS(int[][] maze, int[] start, int[] destination) {
        if (maze == null || maze.length == 0 || maze[0] == null || maze[0].length == 0) {
            return false;
        }
        int rows = maze.length, cols = maze[0].length;
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(start);
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        visited[start[0]][start[1]] = true;
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();

            if (Arrays.equals(cur, destination)) return true;
            int r = cur[0], c = cur[1];
            for (int[] dir : dirs) {
                int nr = r + dir[0], nc = c + dir[1];
                while (nr >= 0 && nr < rows && nc >= 0 && nc < cols && maze[nr][nc] != 1) {
                    nr = nr + dir[0];
                    nc = nc + dir[1];
                }
                nr = nr - dir[0];
                nc = nc - dir[1];
                if (!visited[nr][nc]) {
                    visited[nr][nc] = true;
                    queue.add(new int[]{nr, nc});
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int[][] maze = {
                {0,0,1,0,0},
                {0,0,0,0,0},
                {0,0,0,1,0},
                {1,1,0,1,1},
                {0,0,0,0,0}};
        int[] start = {0,4};
        int[] destination = {4,4};
        System.out.println(hasPath(maze, start, destination));
    }
}
