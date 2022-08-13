package google.vo.mianjing;

import java.util.Arrays;

public class BricksFallingWhenHit {
    public int[] hitBricks(int[][] grid, int[][] hits) {
        int m = grid.length, n = grid[0].length;
        for (int[] hit : hits) {
            int i = hit[0], j = hit[1];
            if (grid[i][j] == 1) {
                grid[i][j] = 2;
            }
        }
        Union union = new Union(m * n + 1);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    unionCell(i, j, union, grid);
                }
            }
        }
        int count = union.size[union.find(0)];
        int[] result = new int[hits.length];
        for (int i = hits.length - 1; i >= 0; i--) {
            int[] hit = hits[i];
            int x = hit[0], y = hit[1];
            if (grid[x][y] == 2) {
                unionCell(x, y, union, grid);
                grid[x][y] = 1;
            }
            int newSize = union.size[union.find(0)];
            result[i] = newSize - count > 1 ? newSize - count - 1 : 0;
            count = newSize;
        }
        return result;
    }

    private void unionCell(int i, int j, Union union, int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        for (int[] dir : dirs) {
            int ni = i + dir[0], nj = j + dir[1];
            if (ni >= m || ni < 0 || nj >= n || nj < 0 || grid[ni][nj] != 1) {
                continue;
            }
            union.union(i * n + j + 1, ni * n + nj + 1);
        }
        if (i == 0) {
            union.union(i * n + j + 1, 0);
        }
    }

    class Union {
        int count;
        int[] size, father;
        public Union(int n) {
            this.count = n;
            this.size = new int[n];
            this.father = new int[n];
            Arrays.fill(size, 1);
            for (int i = 0; i < n; i++) {
                father[i] = i;
            }
        }

        public int find(int a) {
            while (father[a] != a) {
                a = father[a];
            }
            return a;
        }

        public void union(int a, int b) {
            int fa = find(a);
            int fb = find(b);
            if (fa == fb)
                return;
            count--;
            int sizeFa = size[fa];
            int sizeFb = size[fb];
            if (sizeFa > sizeFb) {
                father[fb] = fa;
                size[fa] += sizeFb;
            }  else {
                father[fa] = fb;
                size[fb] += sizeFa;
            }
        }
    }
}
