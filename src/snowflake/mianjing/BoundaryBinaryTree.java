package snowflake.mianjing;

import Bloomberg.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
Problem Requirements
You are given the root of a binary tree. Your goal is to return a list of values representing the boundary of the tree. You must trace this boundary in an anti-clockwise (counter-clockwise) direction, starting from the root.

The boundary consists of four specific parts:

The Root: The top node of the tree.
The Left Boundary: The nodes along the left edge. Note: Do not include leaf nodes (nodes with no children) in this part.
The Leaves: All the bottom nodes that have no children, listed from left to right.
The Right Boundary: The nodes along the right edge. Note: These are added in reverse order (bottom to top) and do not include leaf nodes.
Special Rule: If the root node has no left or right subtree (it has no children), then the boundary is just the root itself.

Test Cases
Case 1:

Input: root = [1,null,2,3,4]
Output: [1,3,4,2]
Case 2:

Input: root = [1,2,3,4,5,6,null,null,null,7,8,9,10]
Output: [1,2,4,7,8,9,10,6,3]
Technical Constraints
Tree Size: The number of nodes is between 1 and 10^4 (10,000).
Value Range: Each Node.val is between -1000 and 1000.
 */
/**
 * Anti-clockwise boundary traversal of a binary tree.
 *
 * Build the result in 4 stages:
 *   1. Root.
 *   2. Left boundary  — top-down, leaves EXCLUDED.
 *   3. Leaves         — left-to-right.
 *   4. Right boundary — bottom-up, leaves EXCLUDED.
 *
 * Why split this way?
 *   - The root is its own thing (it's neither part of the left boundary
 *     "interior" nor the right one).
 *   - Excluding leaves from the left/right boundary prevents double-listing
 *     them — they'll show up in stage 3.
 *   - The right boundary is naturally collected top-down, then reversed
 *     to get bottom-up order.
 *
 * Special case: a one-node tree → the boundary is just that node.
 *
 * Time:  O(n)
 * Space: O(h) recursion + O(n) output
 */
public class BoundaryBinaryTree {

    public List<Integer> boundaryOfBinaryTree(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;

        // Single-node tree (no left, no right): boundary is just the root.
        // We don't want it accidentally treated as both root + leaf.
        if (isLeaf(root)) {
            out.add(root.val);
            return out;
        }

        out.add(root.val);
        addLeftBoundary(root.left, out);
        addLeaves(root.left, out);
        addLeaves(root.right, out);

        // Collect right boundary top-down, then append in reverse.
        List<Integer> rightSide = new ArrayList<>();
        addRightBoundary(root.right, rightSide);
        for (int i = rightSide.size() - 1; i >= 0; i--) {
            out.add(rightSide.get(i));
        }
        return out;
    }

    // Walk the leftmost path: prefer .left; if no .left, take .right.
    // Skip leaves — they'll be added in stage 3.
    private void addLeftBoundary(TreeNode node, List<Integer> out) {
        TreeNode cur = node;
        while (cur != null) {
            if (!isLeaf(cur)) out.add(cur.val);
            cur = (cur.left != null) ? cur.left : cur.right;
        }
    }

    // Symmetric: walk the rightmost path, prefer .right.
    private void addRightBoundary(TreeNode node, List<Integer> out) {
        TreeNode cur = node;
        while (cur != null) {
            if (!isLeaf(cur)) out.add(cur.val);
            cur = (cur.right != null) ? cur.right : cur.left;
        }
    }

    // Standard pre-order leaf collection (left-to-right).
    private void addLeaves(TreeNode node, List<Integer> out) {
        if (node == null) return;
        if (isLeaf(node)) {
            out.add(node.val);
            return;
        }
        addLeaves(node.left, out);
        addLeaves(node.right, out);
    }

    private boolean isLeaf(TreeNode n) {
        return n.left == null && n.right == null;
    }

    // ============================================================
    // Perfect (full + complete + balanced) binary tree variant
    // ============================================================
    /**
     * Boundary traversal specialized for a *perfect* binary tree — every
     * internal node has two children and every leaf sits at the same depth.
     *
     * With that structure we don't need any leaf-detection or recursion:
     *
     *   - Left boundary (top-down, leaves excluded):
     *       follow .left from the root until we reach the level above the
     *       leaves. That's exactly h - 1 nodes (where h = height in nodes).
     *
     *   - Right boundary (bottom-up, leaves excluded):
     *       follow .right symmetrically; reverse for output.
     *
     *   - Leaves (left-to-right):
     *       all nodes on the last level. We can collect them with a single
     *       BFS that stops at the last level — or, since every internal node
     *       has both children, just descend left-most to find the leaf level
     *       and then BFS that one level.
     *
     * Special case: a single-node tree (height 1) is just the root.
     *
     * Time:  O(n)   (each leaf visited once; the two spines are O(log n))
     * Space: O(n/2) for the leaf level
     */
    public List<Integer> boundaryOfPerfectBinaryTree(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;
        if (isLeaf(root)) {
            out.add(root.val);
            return out;
        }

        // 1) Root.
        out.add(root.val);

        // 2) Left spine, top-down, stop one above the leaves.
        //    (root.left is the first interior node; we stop when its .left is null.)
        TreeNode cur = root.left;
        while (cur != null && !isLeaf(cur)) {
            out.add(cur.val);
            cur = cur.left;
        }

        // 3) All leaves, left-to-right. In a perfect tree the leaf level is
        //    just a BFS that stops once we hit the first leaf.
        java.util.Deque<TreeNode> q = new java.util.ArrayDeque<>();
        q.offer(root);
        while (!q.isEmpty() && !isLeaf(q.peek())) {
            int sz = q.size();
            for (int i = 0; i < sz; i++) {
                TreeNode n = q.poll();
                q.offer(n.left);
                q.offer(n.right);
            }
        }
        for (TreeNode leaf : q) out.add(leaf.val);

        // 4) Right spine, collected top-down then reversed.
        List<Integer> rightSide = new ArrayList<>();
        cur = root.right;
        while (cur != null && !isLeaf(cur)) {
            rightSide.add(cur.val);
            cur = cur.right;
        }
        for (int i = rightSide.size() - 1; i >= 0; i--) {
            out.add(rightSide.get(i));
        }
        return out;
    }

    // ============================================================
    // Demo / tests
    // ============================================================

    /** Build a tree from level-order array using Integer (null allowed). */
    private static TreeNode build(Integer... vals) {
        if (vals.length == 0 || vals[0] == null) return null;
        TreeNode root = new TreeNode(vals[0]);
        java.util.Deque<TreeNode> q = new java.util.ArrayDeque<>();
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
        BoundaryBinaryTree s = new BoundaryBinaryTree();

        // Case 1: [1, null, 2, 3, 4]  →  [1, 3, 4, 2]
        //     1
        //      \
        //       2
        //      / \
        //     3   4
        check(s.boundaryOfBinaryTree(build(1, null, 2, 3, 4)),
              Arrays.asList(1, 3, 4, 2), "case 1");

        // Case 2: [1,2,3,4,5,6,null,null,null,7,8,9,10] → [1,2,4,7,8,9,10,6,3]
        //         1
        //        / \
        //       2   3
        //      /\   /
        //     4  5 6
        //       /\ /\
        //      7 8 9 10
        check(s.boundaryOfBinaryTree(build(1, 2, 3, 4, 5, 6, null,
                                           null, null, 7, 8, 9, 10)),
              Arrays.asList(1, 2, 4, 7, 8, 9, 10, 6, 3), "case 2");

        // Single-node tree.
        check(s.boundaryOfBinaryTree(build(42)),
              Arrays.asList(42), "single node");

        // Left-only chain (every left, no right).
        //   1
        //  /
        // 2
        //  \
        //   3   (3 is leaf)
        //
        // Boundary: root 1, left boundary [2] (3 skipped as leaf? actually 3 IS a leaf),
        // leaves [3], right boundary reversed: [].
        // Hmm: walk left from root: 1 → 2 (has right=3) → 3 (leaf, skip). So left = [2].
        // Leaves of root.left: [3]. Right side: none.
        // Result: [1, 2, 3]
        check(s.boundaryOfBinaryTree(build(1, 2, null, null, 3)),
              Arrays.asList(1, 2, 3), "left chain");

        // Root with only a right child that is a leaf.
        //   1
        //    \
        //     2
        // Boundary: [1, 2] (root, no left side, leaf 2, no right interior).
        check(s.boundaryOfBinaryTree(build(1, null, 2)),
              Arrays.asList(1, 2), "root + right leaf");

        // -------- Perfect-tree variant --------

        // Single node.
        check(s.boundaryOfPerfectBinaryTree(build(7)),
              Arrays.asList(7), "perfect: single node");

        // Height 2 (3 nodes):
        //     1
        //    / \
        //   2   3
        // Root + leaves 2,3, no interior left/right.
        check(s.boundaryOfPerfectBinaryTree(build(1, 2, 3)),
              Arrays.asList(1, 2, 3), "perfect: height 2");

        // Height 3 (7 nodes):
        //         1
        //        / \
        //       2   3
        //      /\   /\
        //     4  5 6  7
        // Root: 1
        // Left interior: 2
        // Leaves L→R: 4,5,6,7
        // Right interior (bottom-up): 3
        // → [1, 2, 4, 5, 6, 7, 3]
        check(s.boundaryOfPerfectBinaryTree(build(1, 2, 3, 4, 5, 6, 7)),
              Arrays.asList(1, 2, 4, 5, 6, 7, 3), "perfect: height 3");

        // Height 4 (15 nodes):
        //                1
        //          /          \
        //        2              3
        //      /   \          /   \
        //     4     5        6     7
        //    /\    /\       /\    /\
        //   8 9  10 11    12 13 14 15
        // Root: 1
        // Left interior top-down (skip leaf level): 2, 4
        // Leaves L→R: 8..15
        // Right interior bottom-up: 7, 3
        // → [1, 2, 4, 8, 9, 10, 11, 12, 13, 14, 15, 7, 3]
        check(s.boundaryOfPerfectBinaryTree(
                  build(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)),
              Arrays.asList(1, 2, 4, 8, 9, 10, 11, 12, 13, 14, 15, 7, 3),
              "perfect: height 4");

        // Sanity: the perfect-tree variant should agree with the general
        // algorithm on perfect inputs.
        TreeNode perfect = build(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        check(s.boundaryOfPerfectBinaryTree(perfect),
              s.boundaryOfBinaryTree(perfect),
              "perfect: matches general algorithm");
    }

    private static void check(List<Integer> got, List<Integer> expected, String label) {
        boolean ok = got.equals(expected);
        System.out.println(label + ": " + got + (ok ? "  OK" : "  FAIL (expected " + expected + ")"));
    }
}
