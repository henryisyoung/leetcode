package leetcode.union;

public class GraphValidTree {
    private class Union {
        int pieces;
        int[] size, father;
        public Union(int cap) {
            this.pieces = cap;
            this.size = new int[cap];
            this.father = new int[cap];
            for (int i = 0; i < cap; i++) {
                size[i] = 1;
                father[i] = i;
            }
        }

        public int find(int a) {
            while (a != father[a]) {
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
            pieces--;
            int sizeA = size[aFather], sizeB = size[bFather];
            if (sizeA > sizeB) {
                father[bFather] = aFather;
                size[aFather] += sizeB;
            } else {
                father[aFather] = bFather;
                size[bFather] += sizeA;
            }
        }

        public int query() {
            return pieces;
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
        return union.query() == 1;
    }
}
