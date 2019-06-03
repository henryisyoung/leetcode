package leetcode.graphAndSearch;

import java.util.*;

public class SmallestRectangleEnclosingBlackPixels {
    public int minArea(char[][] image, int x, int y) {
        if (image == null || image.length == 0 || image[0] == null || image[0].length == 0) {
            return 0;
        }
        int rows = image.length, cols = image[0].length;
        int l = Integer.MAX_VALUE, r = Integer.MIN_VALUE, t = Integer.MAX_VALUE, b = Integer.MIN_VALUE;
        boolean[][] visited = new boolean[rows][cols];
        visited[x][y] = true;
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{x, y});
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            l = Math.min(l, cur[1]);
            r = Math.max(r, cur[1]);
            t = Math.min(t, cur[0]);
            b = Math.max(b, cur[0]);
            for (int[] dir : dirs) {
                int nr = cur[0] + dir[0], nc = cur[1] + dir[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !visited[nr][nc] && image[nr][nc] == '1') {
                    visited[nr][nc] = true;
                    queue.add(new int[]{nr, nc});
                }
            }
        }
        return (r - l + 1) * (b - t + 1);
    }

    public int minArea2(char[][] image, int x, int y) {
        int m = image.length, n = image[0].length;
        int left = searchColumns(image, 0, y, 0, m, true);
        int right = searchColumns(image, y + 1, n, 0, m, false);
        int top = searchRows(image, 0, x, left, right, true);
        int bottom = searchRows(image, x + 1, m, left, right, false);
        return (right - left) * (bottom - top);
    }
    private int searchColumns(char[][] image, int i, int j, int top, int bottom, boolean whiteToBlack) {
        while (i != j) {
            int k = top, mid = (i + j) / 2;
            while (k < bottom && image[k][mid] == '0') ++k;
            if (k < bottom == whiteToBlack) // k < bottom means the column mid has black pixel
                j = mid; //search the boundary in the smaller half
            else
                i = mid + 1; //search the boundary in the greater half
        }
        return i;
    }
    private int searchRows(char[][] image, int i, int j, int left, int right, boolean whiteToBlack) {
        while (i != j) {
            int k = left, mid = (i + j) / 2;
            while (k < right && image[mid][k] == '0') ++k;
            if (k < right == whiteToBlack) // k < right means the row mid has black pixel
                j = mid;
            else
                i = mid + 1;
        }
        return i;
    }

    public static void main(String[] args) {
        char[][] image = new char[3][4];
        image[0] = "0010".toCharArray();
        image[1] = "0110".toCharArray();
        image[2] = "0100".toCharArray();
//        System.out.println(Arrays.deepToString(image));
        int x = 0, y = 2;
        SmallestRectangleEnclosingBlackPixels solver = new SmallestRectangleEnclosingBlackPixels();
        System.out.println(solver.minArea(image, x, y));
    }
}
