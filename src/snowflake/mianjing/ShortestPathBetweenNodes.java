package snowflake.mianjing;

import Bloomberg.TreeNode;

import java.util.ArrayDeque;
import java.util.Deque;

/*
Finding the Shortest Path Between Two Nodes in a Binary Tree
Problem Overview
You are provided with the root of a binary tree that contains n nodes. Every node in this tree has a unique value between 1 and n. You are also given two specific numbers: startValue and destValue.

Your task is to find the shortest path to travel from the node labeled startValue to the node labeled destValue. You must return this path as a string.

Use the following letters to represent the movements:

'L': Move down to the left child node.
'R': Move down to the right child node.
'U': Move up to the parent node.
Sample Cases
Case 1:

Input: root = [5,1,2,3,null,6,4], startValue = 3, destValue = 6

Output: "UURL"

Case 2:

Input: root = [2,1], startValue = 2, destValue = 1

Output: "L"

Input Limits
The number of nodes (n) is between 2 and 100,000 (10^5).
Each Node.val is between 1 and n.
All values in the tree are unique (no duplicates).
The startValue is never the same as the destValue.
Both startValue and destValue are guaranteed to be inside the tree.
 */
/**
 * Shortest path between two values in a binary tree, written as a string of
 * 'L' (left child), 'R' (right child), 'U' (parent).
 *
 * Idea
 * ----
 * The shortest path always goes start → LCA → dest, because the tree has no
 * cross edges. Instead of explicitly finding the LCA we use a slick trick:
 *
 *   1. Find the root→start path        (in 'L'/'R' alphabet).
 *   2. Find the root→dest  path        (in 'L'/'R' alphabet).
 *   3. Strip the common prefix         (that's the root→LCA portion).
 *   4. Every remaining char of path→start becomes a 'U' (climb up to LCA).
 *   5. Append the remaining suffix of path→dest unchanged (descend to dest).
 *
 * Why it's correct: after step 3 the two paths diverge at the LCA. Climbing
 * up the start side undoes those L/R steps; the dest side is exactly the
 * descent from LCA to dest.
 *
 * Why it's the SHORTEST: any path between two tree nodes must pass through
 * their LCA (no cycles), and the LCA-routed path uses one move per edge with
 * no detours.
 *
 * Time:  O(n) — two DFS traversals + linear prefix scan.
 * Space: O(h) recursion (or explicit stack) + O(n) path strings.
 */
public class ShortestPathBetweenNodes {

    public String getDirections(TreeNode root, int startValue, int destValue) {
        StringBuilder pathStart = new StringBuilder();
        StringBuilder pathDest  = new StringBuilder();

        findPath(root, startValue, pathStart);
        findPath(root, destValue,  pathDest);

        // Drop the common prefix (root → LCA).
        int i = 0;
        int common = Math.min(pathStart.length(), pathDest.length());
        while (i < common && pathStart.charAt(i) == pathDest.charAt(i)) {
            i++;
        }

        StringBuilder out = new StringBuilder();
        // Climb from start up to LCA: one 'U' per remaining char in pathStart.
        for (int k = pathStart.length() - i; k > 0; k--) {
            out.append('U');
        }
        // Then descend LCA → dest using the suffix of pathDest.
        out.append(pathDest, i, pathDest.length());
        return out.toString();
    }

    /**
     * DFS that appends 'L'/'R' to `path` while searching for `target`.
     * Returns true (and leaves `path` set to root→target) if found.
     * On a miss in a subtree, the appended char is rolled back.
     *
     * Recursive form is concise; for the n=1e5 worst-case skewed tree we use
     * an iterative variant below to avoid stack overflow.
     */
    private boolean findPath(TreeNode root, int target, StringBuilder path) {
        // Iterative DFS with an explicit stack frame so we don't blow the
        // call stack on skewed inputs (n up to 1e5).
        // Each frame remembers which child we still need to visit.
        Deque<Frame> stack = new ArrayDeque<>();
        stack.push(new Frame(root));
        while (!stack.isEmpty()) {
            Frame f = stack.peek();
            if (f.node == null) {
                stack.pop();
                continue;
            }
            if (f.state == 0) {
                if (f.node.val == target) return true;
                f.state = 1;
                path.append('L');
                stack.push(new Frame(f.node.left));
            } else if (f.state == 1) {
                path.deleteCharAt(path.length() - 1);
                f.state = 2;
                path.append('R');
                stack.push(new Frame(f.node.right));
            } else {
                path.deleteCharAt(path.length() - 1);
                stack.pop();
            }
        }
        return false;
    }

    /** Builds root→target path of 'L'/'R' chars into `path`. Returns true iff found. */
    private boolean findPath2(TreeNode node, int target, StringBuilder path) {
        if (node == null) return false;
        if (node.val == target) return true;
        path.append('L');
        if (findPath(node.left, target, path)) return true;
        path.deleteCharAt(path.length() - 1);
        path.append('R');
        if (findPath(node.right, target, path)) return true;
        path.deleteCharAt(path.length() - 1);
        return false;
    }

    private static class Frame {
        TreeNode node;
        int state;             // 0 = before L, 1 = after L (try R), 2 = after R
        Frame(TreeNode n) { this.node = n; }
    }

    // ============================================================
    // Demo / tests
    // ============================================================
    private static TreeNode build(Integer... vals) {
        if (vals.length == 0 || vals[0] == null) return null;
        TreeNode root = new TreeNode(vals[0]);
        Deque<TreeNode> q = new ArrayDeque<>();
        q.offer(root);
        int i = 1;
        while (!q.isEmpty() && i < vals.length) {
            TreeNode cur = q.poll();
            if (i < vals.length && vals[i] != null) {
                cur.left = new TreeNode(vals[i]);
                q.offer(cur.left);
            }
            i++;
            if (i < vals.length && vals[i] != null) {
                cur.right = new TreeNode(vals[i]);
                q.offer(cur.right);
            }
            i++;
        }
        return root;
    }

    public static void main(String[] args) {
        ShortestPathBetweenNodes s = new ShortestPathBetweenNodes();

        // Case 1: root = [5,1,2,3,null,6,4], start=3, dest=6
        //         5
        //        / \
        //       1   2
        //      /   / \
        //     3   6   4
        check(s.getDirections(build(5, 1, 2, 3, null, 6, 4), 3, 6),
              "UURL", "case 1");

        // Case 2: root = [2,1], start=2, dest=1  → just "L"
        check(s.getDirections(build(2, 1), 2, 1),
              "L", "case 2");

        // Start is ancestor of dest: only descents.
        //         5
        //        / \
        //       1   2
        //      /
        //     3
        // 5 → 3  =  "LL"
        check(s.getDirections(build(5, 1, 2, 3), 5, 3),
              "LL", "ancestor → descendant");

        // Dest is ancestor of start: only U's.
        // 3 → 5 in same tree = "UU"
        check(s.getDirections(build(5, 1, 2, 3), 3, 5),
              "UU", "descendant → ancestor");

        // Path that bends at a non-root LCA.
        //         1
        //        / \
        //       2   3
        //      /\    \
        //     4  5    6
        //         \
        //          7
        // 4 → 7 :  4 up to LCA(=2), then down-right to 5, then right to 7
        //       =  U R R
        check(s.getDirections(build(1, 2, 3, 4, 5, null, 6, null, null, null, 7),
                              4, 7),
              "URR", "bend at non-root LCA");

        // Now a real cross-root case:
        //         1
        //        / \
        //       2   3
        //      /     \
        //     4       6
        //              \
        //               7
        // 4 → 7  : up, up, then RRR? Let's compute:
        //   path(4) = LL, path(7) = RRR
        //   common prefix = "" → result = U U + R R R = "UURRR"
        check(s.getDirections(build(1, 2, 3, 4, null, null, 6, null, null, null, 7),
                              4, 7),
              "UURRR", "cross-root LCA");
    }

    private static void check(String got, String expected, String label) {
        boolean ok = got.equals(expected);
        System.out.println(label + ": \"" + got + "\""
                + (ok ? "  OK" : "  FAIL (expected \"" + expected + "\")"));
    }
}
