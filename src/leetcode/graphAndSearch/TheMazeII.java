package leetcode.graphAndSearch;

import java.util.*;

public class TheMazeII {
    public static int shortestDistance(int[][] maze, int[] start, int[] destination) {
        if (maze == null || maze.length == 0 || maze[0] == null || maze[0].length == 0) {
            return -1;
        }
        int rows = maze.length, cols = maze[0].length;
        int[][] dist = new int[rows][cols];
        for (int[] arr : dist) {
            Arrays.fill(arr, Integer.MAX_VALUE);
        }
        Queue<int[]> queue = new LinkedList<>();
        dist[start[0]][start[1]] = 0;
        queue.add(start);
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int curDist = dist[cur[0]][cur[1]];
            for (int[] dir : dirs) {
                int nr = cur[0] + dir[0], nc = cur[1] + dir[1], count = 1;
                while (nr >= 0 && nr < rows && nc >= 0 && nc < cols && maze[nr][nc] == 0) {
                    nr += dir[0];
                    nc += dir[1];
                    count++;
                }
                count--;
                nr -= dir[0];
                nc -= dir[1];
                if (curDist + count < dist[nr][nc]) {
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
