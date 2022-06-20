package datastructure.graph;

public class GraphValidTree {
    class Union{
        int[] father, size;
        int count;
        public Union(int n) {
            this.count = n;
            this.father = new int[n];
            this.size = new int[n];
            for (int i = 0; i < n; i++) {
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
            int fa = father[a], fb = father[b];
            if (fa == fb) return;
            int sizeA = size[fa], sizeB = size[fb];
            count--;
            if (sizeA > sizeB) {
                father[fb] = fa;
                size[fa] += sizeB;
            } else {
                father[fa] = fb;
                size[fb] += sizeA;
            }
        }
    }

    public boolean validTree(int n, int[][] edges) {
        Union union = new Union(n);

        for (int[] edge : edges) {
            int a = edge[0], b = edge[1];
            int fa = union.find(a), fb = union.find(b);
            if (fa == fb) return false;
            union.union(a, b);
        }
        return union.count == 1;
    }
}
