package snap;

import java.util.Arrays;
import java.util.*;

public class SimilarStringGroups {
    public int numSimilarGroups(String[] A) {
        int count = A.length;
        Union union = new Union(count);
        Map<String, List<String>> map = new HashMap<>();
        for (int i = 0; i < A.length - 1; i++) {
            for (int j = i + 1; j < A.length; j++) {
                if (similar(A[i], A[j])) {
                    union.union(i, j);
                }
            }
        }
        return union.count;
    }

    public boolean similar(String word1, String word2) {
        int diff = 0;
        for (int i = 0; i < word1.length(); ++i)
            if (word1.charAt(i) != word2.charAt(i))
                diff++;
        return diff <= 2;
    }

    class Union{
        int[] size, father;
        int count;
        public Union(int n) {
            this.count = n;
            this.size = new int[n];
            this.father = new int[n];
            Arrays.fill(size, 1);
            for (int i = 0; i < n; i++) {
                father[i] = i;
            }
        }

        public int find(int a) {
            while (a != father[a]) {
                a= father[a];
            }
            return a;
        }

        public void union(int a, int b) {
            int fa = find(a);
            int fb = find(b);
            if (fa == fb) return;
            count--;
            int sizeFa = size[fa];
            int sizeFb = size[fb];
            if (sizeFa > sizeFb) {
                size[fa] += sizeFb;
                father[fb] = fa;
            } else {
                size[fb] += sizeFa;
                father[fa] = fb;
            }
        }
    }
}
