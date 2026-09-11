package waymo;

/*
Given a list of directed edges in (parent, child) form, decide whether they form
a valid forest.

A valid forest means:
  1. A node can have at most one parent.
  2. There is no cycle.
  3. Multiple roots are allowed.

Examples:
  [(1,2), (1,3), (4,5)]       -> true   two trees: 1-rooted and 4-rooted
  [(1,2), (3,2)]              -> false  node 2 has two parents
  [(1,2), (2,3), (3,1)]       -> false  cycle
  [(1,1)]                     -> false  self-loop

Assumption:
  Duplicate pairs are treated as invalid input. If an interviewer wants
  duplicate edges to be ignored, replace the duplicate-edge return false with
  continue.
*/

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
Algorithm: parent-count check + union-find cycle check.

For each edge parent -> child:
  1. Reject self-loop.
  2. Reject duplicate edge.
  3. Reject if child already has a different parent.
  4. Union parent and child as an undirected edge. If they were already in the
     same connected component, adding this edge creates a cycle.

Why union-find works here:
  In an undirected graph, a forest is exactly an acyclic graph. The separate
  child-parent map enforces the directed-tree rule that each child has at most
  one parent. Together, those two checks characterize a directed forest.

Complexity:
  Time:   O(E * alpha(V)), effectively O(E)
  Memory: O(V + E)
*/
public class ValidForestFromParentChildPairs {

    public boolean isValidForest(int[][] pairs) {
        if (pairs == null) return false;

        UnionFind uf = new UnionFind();
        Map<Integer, Integer> parentOfChild = new HashMap<>();
        Set<String> seenEdges = new HashSet<>();

        for (int[] pair : pairs) {
            if (pair == null || pair.length != 2) return false;

            int parent = pair[0];
            int child = pair[1];
            if (parent == child) return false;

            String edgeKey = edgeKey(parent, child);
            if (!seenEdges.add(edgeKey)) return false;

            if (parentOfChild.containsKey(child)) return false;
            parentOfChild.put(child, parent);

            uf.add(parent);
            uf.add(child);
            if (uf.find(parent) == uf.find(child)) return false;
            uf.union(parent, child);
        }

        return true;
    }

    private String edgeKey(int parent, int child) {
        return parent + "->" + child;
    }

    private static final class UnionFind {
        private final Map<Integer, Integer> parent = new HashMap<>();
        private final Map<Integer, Integer> size = new HashMap<>();

        void add(int x) {
            if (!parent.containsKey(x)) {
                parent.put(x, x);
                size.put(x, 1);
            }
        }

        int find(int x) {
            while (parent.get(x) != x) {
                x = parent.get(x);
            }
            return x;
        }

        void union(int a, int b) {
            int ra = find(a);
            int rb = find(b);
            if (ra == rb) return;

            if (size.get(ra) < size.get(rb)) {
                parent.put(ra, rb);
                size.put(rb, size.get(rb) + size.get(ra));
            } else {
                parent.put(rb, ra);
                size.put(ra, size.get(ra) + size.get(rb));
            }
        }
    }

    /* --------------------------- demo / tests --------------------------- */

    public static void main(String[] args) {
        ValidForestFromParentChildPairs solver = new ValidForestFromParentChildPairs();

        test(solver, "empty input is an empty forest", new int[][]{}, true);

        test(solver, "single tree",
                new int[][]{{1, 2}, {1, 3}, {3, 4}, {3, 5}},
                true);

        test(solver, "multiple trees",
                new int[][]{{1, 2}, {1, 3}, {10, 11}, {10, 12}},
                true);

        test(solver, "child has two parents",
                new int[][]{{1, 3}, {2, 3}},
                false);

        test(solver, "directed cycle",
                new int[][]{{1, 2}, {2, 3}, {3, 1}},
                false);

        test(solver, "undirected cycle even though indegree is one",
                new int[][]{{1, 2}, {1, 3}, {2, 4}, {3, 4}},
                false);

        test(solver, "self-loop",
                new int[][]{{7, 7}},
                false);

        test(solver, "duplicate edge",
                new int[][]{{1, 2}, {1, 2}},
                false);

        test(solver, "non-contiguous and negative node ids",
                new int[][]{{100, -1}, {-1, 42}, {500, 600}},
                true);

        System.out.println("All tests passed.");
    }

    private static void test(ValidForestFromParentChildPairs solver, String name, int[][] pairs, boolean expected) {
        boolean actual = solver.isValidForest(pairs);
        if (actual != expected) {
            throw new AssertionError(name + " expected " + expected + " but got " + actual);
        }
        System.out.println(name + ": " + actual);
    }
}
