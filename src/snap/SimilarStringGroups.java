package snap;

public class SimilarStringGroups {
    class Union{
        int count;
        int[] size, father;
        public Union(int n) {
            this.count = n;
            this.father = new int[n];
            this.size = new int[n];
            for (int i = 0; i < n; i++) {
                size[i] = 1;
                father[i] = i;
            }
        }

        public int find(int a) {
            while (a != father[a]) a= father[a];
            return a;
        }

        public void union(int a, int b) {
            int fa = find(a), fb = find(b);
            if (fa == fb) return;
            count--;
            int sizeFa = size[fa], sizeFb = size[fb];
            if (sizeFa > sizeFb) {
                size[fa] += sizeFb;
                father[fb] = fa;
            } else {
                size[fb] += sizeFa;
                father[fa] = fb;
            }
        }
    }
    public int numSimilarGroups(String[] A) {
        if (A == null || A.length == 0) return 0;
        Union union = new Union(A.length);
        for (int i = 0; i < A.length - 1; i++) {
            for (int j = i + 1; j < A.length; j++) {
                String a = A[i], b = A[j];
                if (isSimilar(a, b)) {
                    union.union(i, j);
                }
            }
        }
        return union.count;
    }

    private boolean isSimilar(String a, String b) {
        int count = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) count++;
        }
        return count <= 2;
    }
}
