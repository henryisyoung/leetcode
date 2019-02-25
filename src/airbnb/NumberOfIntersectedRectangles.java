package airbnb;

import java.util.HashSet;
import java.util.Set;

public class NumberOfIntersectedRectangles {
    private boolean intersect(int[][] r1, int[][] r2) {
        return (r1[0][0] < r2[0][0] && r1[0][1] < r2[0][1] && r1[1][0] > r2[0][0] && r1[1][1] > r2[0][1]) ||
                (r1[0][0] < r2[1][0] && r1[0][1] < r2[1][1] && r1[1][0] > r2[1][0] && r1[1][1] > r2[1][1]);
    }

    private int find(int val, int[] parents) {
        while (parents[val] != val) {
            val = parents[val];
        }
        return val;
    }

    public int countIntersection(int[][][] rectangles) {
        int[] parents = new int[rectangles.length];
        for (int i = 0; i < parents.length; i++) {
            parents[i] = i;
        }
        for (int i = 0; i < rectangles.length; i++) {
            for (int j = i + 1; j < rectangles.length; j++) {
                if (intersect(rectangles[i], rectangles[j])) {
                    int root1 = find(i, parents);
                    int root2 = find(j, parents);
                    if (root1 != root2) {
                        parents[root1] = root2;
                    }
                }
            }
        }
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < parents.length; i++) {
            set.add(find(i, parents));
        }
        return set.size();
    }
}
