package leetcode.graphAndSearch;

import java.util.ArrayList;
import java.util.List;

public class NumberIslandsII {
    class Union {
        int n, count;
        int[] father, size;
        public Union(int n) {
            this.n = n;
            this.count = n - 1;
            this.father = new int[n];
            this.size = new int[n];
            for (int i = 0; i < n; i++) {
                father[i] = i;
                size[i] = 1;
            }
        }

        public int find(int a) {
            while (a != father[a]) {
                a = father[a];
            }
            return a;
        }

        public void union(int a, int b) {
            int afather = find(a);
            int bfather = find(b);
            if (afather != bfather) {
                count--;
                int sizeA = size[afather], sizeB = size[bfather];
                if (sizeA > sizeB) {
                    size[afather] += sizeB;
                    father[bfather] = afather;
                } else {
                    size[bfather] += sizeA;
                    father[afather] = bfather;
                }
            }
        }

        public int size() {
            return count;
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
            count++;
            int r = pos[0], c = pos[1];
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
