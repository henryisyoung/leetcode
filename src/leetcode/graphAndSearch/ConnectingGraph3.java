package leetcode.graphAndSearch;

public class ConnectingGraph3 {
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
    Union union;
    /*
     * @param n: An integer
     */public ConnectingGraph3(int n) {
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
     * @return: An integer
     */
    public int query() {
        // write your code here
        return union.size();
    }
}
