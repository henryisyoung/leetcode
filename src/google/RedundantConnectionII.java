package google;

import java.util.Arrays;
/*
There are basic 3 cases, which divided by two conditions
case 1 有环，没有入度为2的结点
5 <- 1 -> 2
     ^    |
     |    v
     4 <- 3

case 2 有环，且有入度为2的结点（结点1）
     4
    /
   v
   1
 /  ^
v    \
2 -->3

case 3 无环，但是有结点入度为2的结点（结点3）
  1
 / \
v   v
2-->3

对于这三种情况的处理方法各不相同，首先对于第一种情况，我们返回的产生入度为2的后加入的那条边[2, 3]，而对于第二种情况，我们返回的是刚好组成环的最后加入的那条边[4, 1]，
最后对于第三种情况我们返回的是组成环，且组成入度为2的那条边[3, 1]。

明白了这些，我们先来找入度为2的点，如果有的话，那么我们将当前产生入度为2的后加入的那条边标记为second，前一条边标记为first。然后我们来找环，为了方便起见，
找环使用联合查找Union Find的方法，可参见Redundant Connection中的解法三。当我们找到了环之后，如果first不存在，
说明是第二种情况，我们返回刚好组成环的最后加入的那条边。如果first存在，说明是第三种情况，我们返回first。如果没有环存在，说明是第一种情况，我们返回second
*/

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
