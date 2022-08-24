package uber.VO;

import java.util.ArrayList;
import java.util.List;

public class NumberIslandsII {

    private class Union {
        int capacity;
        int[] size, father;

        public Union (int cap) {
            this.capacity = cap;
            this.size = new int[cap];
            this.father = new int[cap];
            for (int i = 0; i < cap; i++) {
                father[i] = i;
                size[i] = 1;
            }
        }

        public int find(int a) {
            while (father[a] != a) {
                a = father[a];
            }
            return a;
        }

        public void union(int a, int b) {
            int aFather = find(a);
            int bFather = find(b);
            if (aFather == bFather) {
                return;
            }
            int aSize = size[aFather], bSize = size[bFather];
            if (aSize > bSize) {
                father[bFather] = aFather;
                size[aFather] += bSize;
            } else {
                father[aFather] = bFather;
                size[bFather] += aSize;
            }
        }
    }
    public List<Integer> numIslands2(int m, int n, int[][] positions) {
        List<Integer> result = new ArrayList<>();
        if (m * n == 0 || positions == null || positions.length == 0) {
            return result;
        }
        int[][] table = new int[m][n];
        int count = 0;
        Union union = new Union(m * n);
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] pos : positions) {
            int r = pos[0], c = pos[1];
            if (table[r][c] == 1) {
                result.add(count);
                continue;
            }
            count++;
            table[r][c] = 1;
            for (int[] dir : dirs) {
                int nr = r + dir[0], nc = c + dir[1];
                if (nr >=0 && nr < m && nc >= 0 && nc < n && table[nr][nc] == 1) {
                    int nFather = union.find(nr * n + nc);
                    int oFather = union.find(r * n + c);
                    if (nFather != oFather) {
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
