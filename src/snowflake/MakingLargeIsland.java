package snowflake;

import java.util.HashSet;
import java.util.Set;

public class MakingLargeIsland {
    static class Union {
        int[] size;
        int[] father;

        public Union(int capacity) {
            this.size = new int[capacity];
            this.father = new int[capacity];

            for (int  i = 0; i < capacity; i++) {
                father[i] = i;
                size[i] = 1;
            }
        }

        public int find(int x) {
            while (x != father[x]) {
                x = father[x];
            }

            return x;
        }

        public void union(int a, int b) {
            int fatherA = find(a);
            int fatherB = find(b);
            if (fatherA == fatherB) return;
            int sizeFa = size[fatherA];
            int sizeFb = size[fatherB];

            if (sizeFa > sizeFb) {
                father[fatherB] = fatherA;
                size[fatherA] += sizeFb;
            } else {
                father[fatherA] = fatherB;
                size[fatherB] += sizeFa;
            }
        }

        public int size(int x) {
            return size[x];
        }
    }
    public int largestIsland(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        Union union = new Union(rows * cols);

        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    int cur = i * cols + j;
                    for (int[] dir : dirs) {
                        int nr = i + dir[0], nc = j + dir[1];
                        if (nr >=0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1) {
                            int next = nr * cols + nc;
                            int curFather = union.find(cur);
                            int nextFather = union.find(next);
                            if (curFather != nextFather) {
                                union.union(cur, next);
                            }
                        }
                    }
                }
            }
        }

        int max = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    // covers the all-ones case where no zero gets flipped
                    max = Math.max(max, union.size(union.find(i * cols + j)));
                    continue;
                }
                int cur = i * cols + j;
                int curMax = 1;
                Set<Integer> visited = new HashSet<>();
                for (int[] dir : dirs) {
                    int nr = i + dir[0], nc = j + dir[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1) {
                        int next = nr * cols + nc;
                        int nextFather = union.find(next);
                        if (!visited.contains(nextFather)) {
                            curMax += union.size(nextFather);
                            visited.add(nextFather);
                        }
                    }
                }
                max = Math.max(max, curMax);
            }
        }
        return max;
    }
}
