package uber;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class FloodFill {
    public static int[][] floodFill(int[][] image, int sr, int sc, int newColor) {
        if(image == null || image.length == 0 || image[0] == null || image[0].length == 0){
            return image;
        }
        int rows = image.length, cols = image[0].length;
        if (sc < 0 | sc >= cols || sr < 0 || sr >= rows || image[sr][sc] == newColor) {
            return image;
        }
        int oldColor = image[sr][sc];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{sr, sc});
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1];
            image[r][c] = newColor;

            for (int[] dir : dirs) {
                int nr = r + dir[0], nc = c + dir[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && image[nr][nc] == oldColor) {
                    queue.add(new int[]{nr, nc});
                }
            }
        }
        return image;
    }

    public static void main(String[] args) {
        int[][] image = {{1,1,1},{1,1,0},{1,0,1}};
        int sr = 1, sc = 1, newColor = 2;
        int[][] result = floodFill(image, sr, sc, newColor );
        System.out.println(Arrays.deepToString(result));
    }
}
