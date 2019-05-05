package leetcode.graphAndSearch;

public class GraphValidTree {
    class Union {
        int n, count;
        int[] father, size;
        public Union(int n) {
            this.n = n;
            this.count = n;
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

    public boolean validTree(int n, int[][] edges) {
        if (n - 1 != edges.length) {
            return false;
        }
        Union union = new Union(n);
        for (int[] edge : edges) {
            int a = edge[0], b = edge[1];
            int aFather = union.find(a), bFather = union.find(b);
            if (aFather == bFather) {
                return false;
            }
            union.union(a, b);
        }
        return union.size() == 1;
    }
}
