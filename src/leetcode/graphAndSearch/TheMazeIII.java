package leetcode.graphAndSearch;

import java.util.*;

public class TheMazeIII {
    public static String findShortestWay(int[][] maze, int[] ball, int[] hole) {
        int rows = maze.length, cols = maze[0].length;
        int[][] dist = new int[rows][cols];
        for (int[] arr : dist) Arrays.fill(arr, Integer.MAX_VALUE);
        Queue<int[]> queue = new LinkedList<>();
        queue.add(ball);
        dist[ball[0]][ball[1]] = 0;

        int[][] dirs = {{0,-1},{-1,0},{0,1},{1,0}};
        Map<Integer, String> map = new HashMap<>();
        char[] strs = {'l','u','r','d'};
        map.put(ball[0] * cols + ball[1], "");

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1], curDist = dist[r][c];
            String prevPath = map.get(r * cols + c);
            for (int i = 0; i < 4; i++) {
                int[] dir = dirs[i];
                char str = strs[i];
                int nr = r + dir[0], nc = c + dir[1];
                int count = 1;
                while (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !(nr == hole[0] && nc == hole[1])) {
                    nr += dir[0];
                    nc += dir[1];
                    count++;
                }
                if (!(nr == hole[0] && nc == hole[1])) {
                    nr -= dir[0];
                    nc -= dir[1];
                    count--;
                }
                String nextPath = prevPath + str;
                if (dist[nr][nc] > curDist + count) {
                    dist[nr][nc] = curDist + count;
                    map.put(nr * cols + nc, nextPath);
                    if (!(nr == hole[0] && nc == hole[1])) {
                        queue.add(new int[]{nr, nc});
                    }
                } else if (dist[nr][nc] > curDist + count && map.getOrDefault(nr * cols + nc, "").compareTo(nextPath) > 0){
                    map.put(nr * cols + nc, nextPath);
                    if (!(nr == hole[0] && nc == hole[1])) {
                        queue.add(new int[]{nr, nc});
                    }
                }

            }
        }

        return map.getOrDefault(hole[0] * cols + hole[1],"impossible");
    }

    public static void main(String[] args) {
        int[][] maze = {{0,0,0,0,0},{1,1,0,0,1},{0,0,0,0,0},{0,1,0,0,1},{0,1,0,0,0}};

        int[] start = {4,3};
        int[] destination = {0,1};
        System.out.println(findShortestWay(maze, start, destination));
    }
}
