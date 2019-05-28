package google;

import java.util.Arrays;

public class RedundantConnectionII {
    static class Union{
        int[] father, size;
        int capacity;
        public Union(int capacity) {
            this.capacity = capacity;
            this.size = new int[capacity + 1];
            this.father = new int[capacity + 1];
            for (int i = 1; i <= capacity; i++) {
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

        public void union(int a, int b) {
            int fa = find(a), fb = find(b);
            if (fa == fb) {
                return;
            }
            int faSize = size[fa], fbSize = size[fb];
            if (faSize > fbSize) {
                father[fb] = fa;
                size[fa] += fbSize;
            } else {
                father[fa] = fb;
                size[fb] += faSize;
            }
        }

    }
    public static int[] findRedundantConnection(int[][] edges) {
        int[] first = null, second = null;
        int n = 0;
        for (int[] edge : edges) {
            n = Math.max(edge[0], Math.max(n, edge[1]));
        }
        Union union = new Union(n);
        int[] root = new int[n + 1];
        for (int[] edge : edges) {
            if (root[edge[1]] == 0) {
                root[edge[1]] = edge[0];
            } else {
                first = new int[]{root[edge[1]], edge[1]};
                second = edge;
                edge[1] = 0;
            }
        }
        for (int[] edge : edges) {
            int a = edge[0], b = edge[1];
            int fa = union.find(a), fb = union.find(b);
            if (fa == fb) {
                if (first == null) {
                    return edge;
                } else {
                    return first;
                }
            }
        }
        return second;
    }

    public static void main(String[] args) {
        int[][] edges1 = {{1,2}, {1,3}, {2,3}},
                edges2 = {{1,2}, {2,3}, {3,4}, {4,1}, {1,5}},
                edges3 = {{1,2},{2,3},{3,1},{4,1}};
        int[] result = findRedundantConnection(edges3);
        System.out.println(Arrays.toString(result));
    }
}
