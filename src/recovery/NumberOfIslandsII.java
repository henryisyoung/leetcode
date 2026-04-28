package recovery;

import java.util.ArrayList;
import java.util.List;

public class NumberOfIslandsII {
    static class Union {
        int capacity;
        int[] father, size;

        public Union(int capacity) {
            this.capacity = capacity;
            this.father = new int[capacity];
            this.size = new int[capacity];
            for (int i = 0; i < capacity; i++) {
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

        public void union(int x, int y) {
            int fatherX = find(x);
            int fatherY = find(y);

            if (fatherX == fatherY) return;
            int sizeFX = size[fatherX];
            int sizeFY = size[fatherY];
            if (sizeFX > sizeFY) {
                father[fatherY] = fatherX;
                size[fatherX] += sizeFY;
            } else {
                father[fatherX] = fatherY;
                size[fatherY] += sizeFX;
            }
        }
    }

    public List<Integer> numIslands2(int m, int n, int[][] positions) {
        List<Integer> result = new ArrayList<>();
        if (positions == null || positions.length == 0) {
            return result;
        }
        Union union = new Union(m * n);
        boolean[][] isIsland = new boolean[m][n];
        int count = 0;
        int[][] dirs = {{1,0},{0,1},{0,-1},{-1,0}};
        for (int[] pos : positions) {
            count++;
            int r = pos[0], c = pos[1];
            isIsland[r][c] = true;
            for (int[] dir : dirs) {
                int nr = r + dir[0], nc = c + dir[1];

                if (nr >= 0 && nr < m && nc >= 0 && nc < n && isIsland[nr][nc]) {
                    int nbIsland = union.find(nr * n + nc);
                    int curIsland = union.find(r * n + c);
                    if (nbIsland != curIsland) {
                        count--;
                        union.union(nr * n + nc, r * n + c);
                    }
                }
            }
            result.add(count);
        }
        return result;
    }
}