package snowflake.mianjing;

/*
Problem Statement
You have a tree with n nodes. The nodes are numbered from 0 to n - 1. The tree structure is defined by a list of edges, where each item looks like [parent, child].

You are given:

The edges list.
The root node of the tree.
An array called invalid that lists nodes we should ignore.
Your goal is to perform a preorder traversal of the tree. However, you must exclude any node found in the invalid list from your final result.

Rules for Traversal
Standard Preorder: Visit the current node before visiting its children.
Child Order: You must visit children in the exact order they are listed in the edges array.
Skipping Nodes: If a node is listed in invalid, do not add it to your result list. However, do not stop there; you must still continue to visit that node's children.
Sample Cases
Example 1

Input: n = 7, edges = [[0,1],[0,2],[1,3],[1,4],[2,5],[2,6]], root = 0, invalid = [1,6]
Output: [0,3,4,2,5]
Explanation: The standard preorder path is 0 -> 1 -> 3 -> 4 -> 2 -> 5 -> 6. Since 1 and 6 are in the invalid list, we simply remove them from the final answer.
Example 2

Input: n = 5, edges = [[0,1],[0,2],[1,3],[1,4]], root = 0, invalid = [0,4]
Output: [1,3,2]
Explanation: The root node 0 is invalid, so we do not include it. We still proceed to process its children (1 and 2). Node 4 is also skipped.
Input Limits
1 <= n <= 10^5
edges.length == n - 1
0 <= parent, child < n
The input always creates a valid rooted tree.
0 <= root < n
0 <= invalid.length <= n
0 <= invalid[i] < n
All node IDs in invalid are unique.
 */


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreorderTraversalWithoutInvalidNodes {

    /*
     * Approach:
     *   1. Build adjacency list `children[parent] = list of children` from the
     *      edges array, preserving the input order so we visit children in
     *      the requested left-to-right sequence.
     *   2. Put `invalid` into a HashSet for O(1) lookup.
     *   3. Iterative preorder DFS from `root`:
     *        - Pop a node; if it's NOT invalid, append to result.
     *        - Push its children in REVERSE order so the leftmost child is
     *          processed next (LIFO stack semantics).
     *      Invalid nodes are skipped from the output but their subtrees are
     *      still traversed (just don't return early).
     *
     * Iterative (not recursive) is intentional — n can be up to 1e5 and a
     * skewed tree would blow Java's default ~10k recursion depth.
     *
     * Time:  O(n + e) = O(n) since e = n - 1 in a tree.
     * Space: O(n) for adjacency list + invalid set + stack.
     */
    public List<Integer> preorder(int n, int[][] edges, int root, int[] invalid) {
        List<List<Integer>> children = new ArrayList<>(n);
        for (int i = 0; i < n; i++) children.add(new ArrayList<>());
        for (int[] e : edges) {
            children.get(e[0]).add(e[1]);
        }

        Set<Integer> invalidSet = new HashSet<>();
        for (int v : invalid) invalidSet.add(v);

        List<Integer> result = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            int node = stack.pop();
            if (!invalidSet.contains(node)) {
                result.add(node);
            }
            // Push children in reverse so leftmost is on top of the stack.
            List<Integer> kids = children.get(node);
            for (int i = kids.size() - 1; i >= 0; i--) {
                stack.push(kids.get(i));
            }
        }
        return result;
    }

    /*
     * Recursive variant — clearer / shorter, but risks StackOverflowError
     * for skewed trees with n ~ 1e5 (Java's default thread stack holds
     * roughly ~10k frames). Use the iterative version above for production.
     */
    public List<Integer> preorderRecursive(int n, int[][] edges, int root, int[] invalid) {
        List<List<Integer>> children = new ArrayList<>(n);
        for (int i = 0; i < n; i++) children.add(new ArrayList<>());
        for (int[] e : edges) children.get(e[0]).add(e[1]);

        Set<Integer> invalidSet = new HashSet<>();
        for (int v : invalid) invalidSet.add(v);

        List<Integer> result = new ArrayList<>();
        dfs(root, children, invalidSet, result);
        return result;
    }

    private void dfs(int node, List<List<Integer>> children,
                     Set<Integer> invalidSet, List<Integer> result) {
        if (!invalidSet.contains(node)) {
            result.add(node);
        }
        // Always recurse into children, even if `node` itself was invalid.
        for (int child : children.get(node)) {
            dfs(child, children, invalidSet, result);
        }
    }

    public static void main(String[] args) {
        PreorderTraversalWithoutInvalidNodes s = new PreorderTraversalWithoutInvalidNodes();

        int[][] cases = {
                // n, root, invalid count + flag for which test (just for printing layout)
        };

        // ---- Iterative ----
        System.out.println("Iterative:");
        runAll(s, false);

        // ---- Recursive ----
        System.out.println("Recursive:");
        runAll(s, true);
    }

    private static void runAll(PreorderTraversalWithoutInvalidNodes s, boolean recursive) {
        System.out.println(call(s, recursive, 7,
                new int[][]{{0,1},{0,2},{1,3},{1,4},{2,5},{2,6}},
                0, new int[]{1, 6}));         // [0, 3, 4, 2, 5]

        System.out.println(call(s, recursive, 5,
                new int[][]{{0,1},{0,2},{1,3},{1,4}},
                0, new int[]{0, 4}));          // [1, 3, 2]

        System.out.println(call(s, recursive, 3,
                new int[][]{{0,1},{0,2}},
                0, new int[]{}));               // [0, 1, 2]

        System.out.println(call(s, recursive, 3,
                new int[][]{{0,1},{0,2}},
                0, new int[]{0, 1, 2}));       // []

        System.out.println(call(s, recursive, 1, new int[][]{}, 0, new int[]{}));   // [0]
        System.out.println(call(s, recursive, 1, new int[][]{}, 0, new int[]{0})); // []
    }

    private static List<Integer> call(PreorderTraversalWithoutInvalidNodes s, boolean recursive,
                                      int n, int[][] edges, int root, int[] invalid) {
        return recursive ? s.preorderRecursive(n, edges, root, invalid)
                         : s.preorder(n, edges, root, invalid);
    }
}
