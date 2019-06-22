package leetcode.graphAndSearch;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class TheMazeIII {
    public String findShortestWay(int[][] maze, int[] ball, int[] hole) {
        // write your code here
        int[][] dists = new int[maze.length][maze[0].length];
        // Initialize the distance array
        for (int[] row: dists) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        dists[ball[0]][ball[1]] = 0;
        int[][] dirs = {{0, 1} ,{0, -1}, {-1, 0}, {1, 0}};
        Queue<int[]> q = new LinkedList<>();
        q.add(ball);
        int m = maze.length, n = maze[0].length;
        String[] u = new String[m * n];
        char[] way = {'l','u','r','d'};
        while (!q.isEmpty()) {
            int[] t = q.poll();
            for (int i = 0; i < 4; ++i) {
                int x = t[0], y = t[1], dist = dists[x][y];
                String path = u[x * n + y];
                while (x >= 0 && x < m && y >= 0 && y < n && maze[x][y] == 0 && (x != hole[0] || y != hole[1])) {
                    x += dirs[i][0]; y += dirs[i][1]; ++dist;
                }
                if (x != hole[0] || y != hole[1]) {
                    x -= dirs[i][0]; y -= dirs[i][1]; --dist;
                }
                path += way[i];
                if (dists[x][y] > dist) {
                    dists[x][y] = dist;
                    u[x * n + y] = path;
                    if (x != hole[0] || y != hole[1]) q.add(new int[]{x, y});
                } else if (dists[x][y] == dist && stringCompare(u[x * n + y], path) > 0) {
                    u[x * n + y] = path;
                    if (x != hole[0] || y != hole[1]) q.add(new int[]{x, y});
                }
            }
        }
        String res = u[hole[0] * n + hole[1]];
        return res.equals("") ? "impossible" : res;
    }

    public  int stringCompare(String str1, String str2) {

        int l1 = str1.length();
        int l2 = str2.length();
        int lmin = Math.min(l1, l2);

        for (int i = 0; i < lmin; i++) {
            int str1_ch = (int)str1.charAt(i);
            int str2_ch = (int)str2.charAt(i);

            if (str1_ch != str2_ch) {
                return str1_ch - str2_ch;
            }
        }

        // Edge case for strings like
        // String 1="Geeks" and String 2="Geeksforgeeks"
        if (l1 != l2) {
            return l1 - l2;
        }

        // If none of the above conditions is true,
        // it implies both the strings are equal
        else {
            return 0;
        }
    }
}
