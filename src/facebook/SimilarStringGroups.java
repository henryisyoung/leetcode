package facebook;

public class SimilarStringGroups {
    class Union{
        int group;
        int[] size, father;
        public Union(int n){
            this.group = n;
            this.size = new int[n];
            this.father = new int[n];
            for (int i = 0; i < n; i++) {
                size[i]  = 1;
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
            int sizeFa = size[fa], sizeFb = size[fb];
            group--;
            if (sizeFa > sizeFb) {
                father[fb] = fa;
                size[fa] += sizeFb;
            } else {
                father[fa] = fb;
                size[fb] += sizeFa;
            }
        }
    }
    public int numSimilarGroups(String[] A) {
        if (A == null || A.length == 0) return 0;
        int n = A.length;
        Union union = new Union(n);

        for (int i = 0; i < A.length - 1; i++) {
            String a = A[i];
            for (int j = i + 1; j < A.length; j++) {
                String b = A[j];
                if(isSimilar(a, b)) {
                    union.union(i, j);
                }
            }
        }

        return union.group;
    }

    private boolean isSimilar(String a, String b) {
        int count = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) count++;
        }
        return count <= 2;
    }
}
