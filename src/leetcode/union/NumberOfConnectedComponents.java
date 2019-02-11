package leetcode.union;

public class NumberOfConnectedComponents {

    private class Union {
         int pieces;
         int[] size, father;

        public Union (int cap) {
            this.pieces = cap;
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
            pieces--;
            int aSize = size[aFather], bSize = size[bFather];
            if (aSize > bSize) {
                father[bFather] = aFather;
                size[aFather] += bSize;
            } else {
                father[aFather] = bFather;
                size[bFather] += aSize;
            }
        }

        public int query(int a) {
            int aFather = find(a);
            return size[aFather];
        }

        public int query() {
            return pieces;
        }
    }

    public int countComponents(int n, int[][] edges) {
        int count = n;
        Union union = new Union(n);
        for (int[] edge : edges) {
            int a = edge[0], b = edge[1];
            int aFather = union.find(a), bFather = union.find(b);
            if (aFather != bFather) {
                count--;
                union.union(a, b);
            }
        }
        return count;
    }
}
