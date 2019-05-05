package leetcode.graphAndSearch;

public class ConnectingGraph {
    class Union {
        int n;
        int[] father, size;
        public Union(int n) {
            this.n = n;
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
    }
    Union union;
    /*
     * @param n: An integer
     */public ConnectingGraph(int n) {
        // do intialization if necessary
        this.union = new Union(n + 1);
    }

    /*
     * @param a: An integer
     * @param b: An integer
     * @return: nothing
     */
    public void connect(int a, int b) {
        // write your code here
        union.union(a, b);
    }

    /*
     * @param a: An integer
     * @param b: An integer
     * @return: A boolean
     */
    public boolean query(int a, int b) {
        // write your code here
        int aFather = union.find(a);
        int bFather = union.find(b);
        return aFather == bFather;
    }
}
