package recovery;

import leetcode.union.Union;

public class GraphValidTree {
    public boolean validTree(int n, int[][] edges) {
        if (n - 1 != edges.length) {
            return false;
        }

        Union union = new Union(n);

        for (int[ ] edge : edges) {
            int a = edge[0], b = edge[1];
            int aFather = union.find(a);
            int bFather = union.find(b);

            if (aFather == bFather) {
                return false;
            }

            union.union(a, b);
        }

        return union.count == 1;
    }
}
