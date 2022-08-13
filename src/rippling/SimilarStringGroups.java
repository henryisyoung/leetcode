package rippling;

import java.util.Arrays;

public class SimilarStringGroups {
    static class Union{
        int[] size, father;
        int count;
        public Union(int n) {
            this.count = n;
            this.father = new int[n];
            this.size = new int[n];
            Arrays.fill(size, 1);
            for (int i = 0; i < n; i++) {
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
            int fa = find(a), fb = find(b);
            if (fa == fb) {
                return;
            }
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

    public static int numSimilarGroups(String[] strs) {
        int n = strs.length;
        Union union = new Union(n);
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (isSimilar(strs[i], strs[j])) {
                    union.union(i, j);
                }
            }
        }
        return union.count;
    }

    private static boolean isSimilar(String s1, String s2) {
        if (s1.equals(s2)) {
            return true;
        }
        if (s1.length() != s2.length()) {
            return false;
        }
        Integer a = -1, b = -1;
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                if (a == -1) {
                    a = i;
                } else if (b == -1) {
                    b = i;
                } else {
                    return false;
                }
            }
        }
        char[] arr = s1.toCharArray();
        char tmp = arr[a];
        arr[a] = arr[b];
        arr[b] = tmp;
        return new String(arr).equals(s2);
    }

    public static void main(String[] args) {
        String[] a = {"tars","rats","arts","star"};
        System.out.println(numSimilarGroups(a));
    }
}
