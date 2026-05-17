package waymo;

import java.util.Arrays;
import java.util.PriorityQueue;

public class PathWithMinimumEffort2 {
    public int minimumEffortPath(int[][] heights) {
        if (heights.length == 0 || heights[0].length == 0) {
            return 0;
        }
        int m = heights.length, n = heights[0].length;
        int[][] dists = new int[m][n];
        for (int[] dist : dists) Arrays.fill(dist, Integer.MAX_VALUE);

        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> (a[2] - b[2]));

        pq.add(new int[]{0, 0, 0});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int r = cur[0], c = cur[1], maxEffort = cur[2];
            if (r == m - 1 && c == n - 1) {
                return maxEffort;
            }

            for (int[] dir : dirs) {
                int nr = dir[0] + r, nc = dir[1] + c;
                if (nr >= 0 && nc >= 0 && nr < m && nc < n) {
                    int curDiff = Math.abs(heights[nr][nc] - heights[r][c]);
                    int localMax = Math.max(curDiff, maxEffort);
                    if (localMax < dists[nr][nc]) {
                        dists[nr][nc] = localMax;
                        pq.add(new int[]{nr, nc, localMax});
                    }
                }
            }
        }

        return dists[m - 1][n - 1];
    }
}


