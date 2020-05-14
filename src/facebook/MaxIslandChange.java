package facebook;

import leetcode.union.Union;

import java.util.HashSet;
import java.util.Set;

public class MaxIslandChange {
    public static int island(int[][] grid) {
        int max = 0;
        int rows = grid.length, cols = grid[0].length;
        Union union = new Union(rows * cols);
        boolean[][] visited = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    dfsUnion(i, j, union, grid, visited);
                }
            }
        }

        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        int r = -1, c = -1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 0) {
                    int area = 1;
                    Set<Integer> set = new HashSet<>();
                    for (int[] dir : dirs) {
                        int nr = i + dir[0], nc = j + dir[1];
                        if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1) {
                            int father = union.find(nr * cols + nc);
                            if (set.contains(father)) continue;
                            area += union.size[father];
                            set.add(father);
                        }
                    }
                    if (area > max){
                        r = i;
                        c = j;
                        max = area;
                        System.out.println("r : " + r + " c: " + c + " max : " + max);
                    }
                }
            }
        }
        return max;
    }

    private static void dfsUnion(int r, int c, Union union, int[][] grid, boolean[][] visited) {
        int rows = grid.length, cols = grid[0].length;
        visited[r][c] = true;
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        for (int[] dir : dirs) {
            int nr = r + dir[0], nc = c + dir[1];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1 && !visited[nr][nc]) {
                union.union(r * cols + c, nr * cols + nc);
                dfsUnion(nr, nc, union, grid, visited);
            }
        }
    }

    public static void main(String[] args) {
        int[][] grid = {
                {0,0,1,0,0,0,0,1,0,0,0,0,0},
                {0,0,0,0,0,0,0,1,1,1,0,0,0},
                {0,1,1,0,1,0,0,0,0,0,0,0,0},
                {0,1,0,0,1,1,0,0,1,0,1,0,0},
                {0,1,0,0,1,1,0,0,1,1,1,0,0},
                {0,0,0,0,0,0,0,0,0,0,1,0,0},
                {0,0,0,0,0,0,0,1,1,1,0,0,0},
                {0,0,0,0,0,0,0,1,1,0,0,0,0}};
        System.out.println(island(grid));
    }
}
