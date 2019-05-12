package pinterest;

import java.util.*;

public class GraphValidTree {
    class Union {
        int count;
        int[] father,size;
        int capacity;
        public Union(int capacity) {
            this.capacity = capacity;
            this.father = new int[capacity];
            this.size = new int[capacity];
            for (int i = 0; i < capacity; i++) {
                father[i] = i;
                size[i] = 1;
            }
            this.count = capacity;
        }

        public int find(int a) {
            while (a != father[a]) {
                a = father[a];
            }
            return a;
        }

        public void union(int a, int b) {
            int aFather = find(a);
            int bFather = find(b);

            if (aFather == bFather) {
                return;
            }
            int afSize = size[aFather], bfSize = size[bFather];
            count--;
            if (afSize > bfSize) {
                size[aFather] += bfSize;
                father[bFather] = aFather;
            } else {
                size[bFather] += afSize;
                father[aFather] = bFather;
            }
        }
    }

    public boolean validTree(int n, int[][] edges) {
        Union union = new Union(n);
        for (int[] edge : edges) {
            int a = edge[0], b = edge[1];
            int af = union.find(a), bf = union.find(b);
            if (af == bf) {
                return false;
            }
            union.union(a, b);
        }

        return union.count == 1;
    }
}
