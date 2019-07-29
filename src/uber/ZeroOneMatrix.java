package uber;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class ZeroOneMatrix {
    public static int[][] updateMatrix(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            return matrix;
        }
        int rows = matrix.length, cols = matrix[0].length;
        int[][] dists = new int[rows][cols];
        for (int[] arr : dists) {
            Arrays.fill(arr, Integer.MAX_VALUE);
        }
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        Queue<int[]> queue = new LinkedList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == 0) {
                    queue.add(new int[]{i, j});
                    dists[i][j] = 0;
                }
            }
        }

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1];
            for (int[] dir : dirs) {
                int nr = cur[0] + dir[0], nc = cur[1] + dir[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && dists[nr][nc] > 1 + dists[r][c]) {
                    dists[nr][nc] = 1 + dists[r][c];
                    queue.add(new int[]{nr, nc});
                }
            }
        }
        return dists;
    }

    public static void main(String[] args) {
        int[][] matrix = {{0,0,0},
                {0,1,0},
                {1,1,1}};
        int[][] result = updateMatrix(matrix);
        System.out.println(Arrays.deepToString(result));
    }
}
