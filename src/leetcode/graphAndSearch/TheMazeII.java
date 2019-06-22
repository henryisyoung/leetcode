package leetcode.graphAndSearch;

import java.util.*;

public class TheMazeII {
    public static int shortestDistance(int[][] maze, int[] start, int[] dest) {
        int[][] distance = new int[maze.length][maze[0].length];
        // Initialize the distance array
        for (int[] row: distance) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        distance[start[0]][start[1]] = 0;
        int[][] DIRS = {{0, 1} ,{0, -1}, {-1, 0}, {1, 0}};
        Queue<int[]> queue = new LinkedList<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            int[] position = queue.poll();
            for (int[] dir: DIRS) {
                int x = position[0];
                int y = position[1];
                int count = 0;

                while (x >= 0 && y >= 0 && x < maze.length && y < maze[0].length && maze[x][y] == 0) {
                    x += dir[0];
                    y += dir[1];
                    count++;
                }

                // Roll Back - When the program break from while loop above,
                // it meas that x, y has been added dir[0], dir[1] one more time and count also has been added 1.
                // But the correct answer (in the range) is less than it, so we should minus dir[0], dir[1] and count-- here.
                x -= dir[0];
                y -= dir[1];
                count--;
                if (distance[position[0]][position[1]] + count < distance[x][y]) {
                    distance[x][y] = distance[position[0]][position[1]] + count;
                    if (x != dest[0] && y != dest[1]) queue.add(new int[] {x, y});
                }
            }
        }

        return distance[dest[0]][dest[1]] == Integer.MAX_VALUE ? -1 : distance[dest[0]][dest[1]];
    }

    public static int shortestDistanceDijkstra(int[][] maze, int[] start, int[] destination) {
        int m = maze.length, n = maze[0].length;
        int[][] dists = new int[m][n];
        for (int[] row: dists) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        int[][] dirs = {{0, 1} ,{0, -1}, {-1, 0}, {1, 0}};

        PriorityQueue<int[]> pq = new PriorityQueue<>(m * n, (a, b) -> (a[2] - b[2]));
        pq.add(new int[]{start[0], start[1], 0});
        dists[start[0]][start[1]] = 0;
        while (!pq.isEmpty()) {
            int[] t = pq.poll();
            if (dists[t[0]][t[1]] < t[2]) continue;
            for (int[] dir : dirs) {
                int x = t[0], y = t[1], dist = dists[t[0]][t[1]];
                while (x >= 0 && x < m && y >= 0 && y < n && maze[x][y] == 0) {
                    x += dir[0];
                    y += dir[1];
                    ++dist;
                }
                x -= dir[0];
                y -= dir[1];
                --dist;
                if (dists[x][y] > dist) {
                    dists[x][y] = dist;
                    if (x != destination[0] || y != destination[1]) pq.add(new int[]{x, y, dist});
                }
            }
        }
        int res = dists[destination[0]][destination[1]];
        return (res == Integer.MAX_VALUE) ? -1 : res;
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
