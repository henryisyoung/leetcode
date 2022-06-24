package apple;

import java.util.LinkedList;
import java.util.Queue;

public class FloodFill {
    public int[][] floodFill(int[][] image, int sr, int sc, int color) {
        int rows = image.length, cols = image[0].length;
        if (image[sr][sc] == color) {
            return image;
        }
        int source = image[sr][sc];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{sr, sc});
        image[sr][sc] = color;
        int[][] dirs = {{1, 0}, {0, 1}, {0, -1}, {-1, 0}};

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            for (int[] dir : dirs) {
                int nr = dir[0] + cur[0], nc = dir[1] + cur[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && image[nr][nc] == source) {
                    queue.add(new int[]{nr, nc});
                    image[nr][nc] = color;
                }
            }
        }
        return image;
    }

    public int[][] floodFillDFS(int[][] image, int sr, int sc, int color) {
        if (image[sr][sc] == color) {
            return image;
        }
        int source = image[sr][sc];
        dfsUpdate(image, sr , sc, color, source);
        return image;
    }

    private void dfsUpdate(int[][] image, int r, int c, int color, int source) {
        int rows = image.length, cols = image[0].length;
        if (r < 0 || r >= rows || c < 0 || c >= cols || image[r][c] != source) {
            return;
        }
        int[][] dirs = {{1, 0}, {0, 1}, {0, -1}, {-1, 0}};
        image[r][c] = color;
        for (int[] dir : dirs) {
            dfsUpdate(image, r + dir[0], c + dir[1], color, source);
        }
    }
}
