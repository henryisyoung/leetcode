package facebook;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class TheMazeII {
    public static int shortestDistance(int[][] maze, int[] start, int[] destination) {
        int rows = maze.length, cols = maze[0].length;
        int[][] dist = new int[rows][cols];
        for (int[] arr : dist) Arrays.fill(arr, Integer.MAX_VALUE);
        Queue<int[]> queue = new LinkedList<>();
        queue.add(start);
        dist[start[0]][start[1]] = 0;
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1];
            int curDist = dist[r][c];
            for (int[] dir : dirs) {
                int nr = r + dir[0], nc = c + dir[1];
                int count = 0;
                while (nr >= 0 && nr < rows && nc >= 0 && nc < cols && maze[nr][nc] == 0) {
                    System.out.println(nr + " " + nc);
                    nr += dir[0];
                    nc += dir[1];
                    count++;
                }
                nr -= dir[0];
                nc -= dir[1];
                if (dist[nr][nc] > curDist + count) {
                    dist[nr][nc] = curDist + count;
                    queue.add(new int[]{nr, nc});
                }
            }
        }

        return dist[destination[0]][destination[1]] == Integer.MAX_VALUE ? -1 : dist[destination[0]][destination[1]];
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
        System.out.println(shortestDistance(maze, start, destination));
    }
}
