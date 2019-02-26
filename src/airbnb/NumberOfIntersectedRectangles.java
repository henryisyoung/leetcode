package airbnb;

import java.util.HashSet;
import java.util.Set;

public class NumberOfIntersectedRectangles {
    private class Union {
        int[] size, father;
        int capacity;
        public Union (int capacity) {
            this.capacity = capacity;
            size = new int[capacity];
            father = new int[capacity];
            for (int i = 0; i < capacity; i++) {
                size[i] = 1;
                father[i] = i;
            }
        }

        public int find(int a) {
            while (a != father[a]) {
                a = father[a];
            }
            return a;
        }

        public void union (int a, int b) {
            int aFather = find(a);
            int bFather = find(b);
            if (aFather == bFather) {
                return;
            }
            int sizeA = size[aFather], sizeB = size[bFather];
            if (sizeA > sizeB) {
                father[bFather] = aFather;
                size[aFather] += sizeB;
            } else {
                father[aFather] = bFather;
                size[bFather] += sizeA;
            }
        }
    }

    public int countIntersection(int[][][] rectangles) {
        if (rectangles == null || rectangles.length == 0 || rectangles[0] == null || rectangles[0].length == 0) {
            return -1;
        }
        int n = rectangles.length, count = n;
        Union un = new Union(n);
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                int[][] r1 = rectangles[i], r2 = rectangles[j];
                if (connectedRectangles(r1, r2)) {
                    int aFather = un.find(i), bFather = un.find(j);
                    if (aFather != bFather) {
                        count--;
                        un.union(aFather, bFather);
                    }
                }
            }
        }
        return count;
    }

    public boolean connectedRectangles(int[][] r1, int[][] r2) {
        int lx1 = r1[0][0], rx1 = r1[1][0], ly1 = r1[0][1], ry1 = r1[1][1];
        int lx2 = r2[0][0], rx2 = r2[1][0], ly2 = r2[0][1], ry2 = r2[1][1];
        if (rx1 < lx2 || rx2 < lx1) {
            return false;
        }
        if (ly1 < ry2 || ly2 < ry1) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        int[][][] rectangles = {{{1,2},{3,1}},{{2,2},{3,0}},{{111,222},{333,33}},{{1,2},{2,0}},{{1,1},{3,0}}};
        int[][] r1 = rectangles[0], r2 = rectangles[1], r3=rectangles[3];
        NumberOfIntersectedRectangles solver = new NumberOfIntersectedRectangles();
        System.out.println(solver.countIntersection(rectangles));
    }
}
