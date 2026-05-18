package waymo;
/*
LeetCode 99: Recover Binary Search Tree.

You are given the root of a BST in which exactly TWO node values were
swapped by mistake.  Recover the tree IN PLACE without changing its
structure.

Examples
  in:  [1, 3, null, null, 2]   (3 and 2 are swapped)
  out: [3, 1, null, null, 2]

  in:  [3, 1, 4, null, null, 2]
  out: [2, 1, 4, null, null, 3]

Constraints
  1 <= n <= 1000   (LC); the algorithm here is O(n) time on any size.
  Follow-up: can you do it in O(1) extra space?  -> Morris traversal below.
*/

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/*
Algorithm: scan the in-order sequence and find the two "out of place" nodes.

  Key fact:
    The in-order traversal of a BST is strictly increasing.  If exactly
    two values were swapped, the in-order sequence contains either:

      Case A (swapped nodes are adjacent):
        Exactly ONE inversion (prev > curr).
        first = prev,  second = curr.

      Case B (swapped nodes are non-adjacent):
        Exactly TWO inversions.
        first  = prev at the FIRST  inversion (the larger one).
        second = curr at the SECOND inversion (the smaller one).

    Either way the rule below picks the right pair in a single pass:

      if prev.val > curr.val:
          if first == null: first = prev
          second = curr               // overwritten on second inversion

  After the scan we swap first.val and second.val — the structure stays
  intact, only two values are exchanged.

  Why "second = curr" outside the if (in pseudocode above I wrote both
  inside the inner branch — equivalent because we only enter the branch
  on an inversion).

Two implementations
  1. Iterative in-order with a stack — O(n) time, O(h) space.
  2. Morris in-order — O(n) time, O(1) extra space (the LC follow-up).
     Uses temporary "thread" pointers from each node's predecessor's
     right child to the node itself, then unthreads on the way back.

Complexity
  Time:   O(n)
  Memory: O(h) for the stack version, O(1) for Morris.
*/
public class RecoverBinarySearchTree {

    public static final class TreeNode {
        public int val;
        public TreeNode left, right;
        public TreeNode(int val) { this.val = val; }
        public TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val; this.left = left; this.right = right;
        }
    }

    /** Stack-based in-order.  O(n) time, O(h) space. */
    public void recoverTree(TreeNode root) {
        if (root == null) return;

        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root, prev = null, first = null, second = null;

        while (cur != null || !stack.isEmpty()) {
            // Walk left-spine, pushing as we go.
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();

            // Visit `cur` in in-order position.
            if (prev != null && prev.val > cur.val) {
                if (first == null) first = prev;     // captures Case A or 1st inversion of Case B
                second = cur;                         // updated again at 2nd inversion in Case B
            }
            prev = cur;
            cur = cur.right;
        }

        if (first != null && second != null) {
            int t = first.val; first.val = second.val; second.val = t;
        }
    }

    /* --------------------------- Morris (O(1) space) reference --------------------------- */

    /** Morris in-order traversal.  O(n) time, O(1) extra space. */
    void recoverTreeMorris(TreeNode root) {
        TreeNode cur = root, prev = null, first = null, second = null;

        while (cur != null) {
            if (cur.left == null) {
                // Visit `cur` (no left subtree to handle first).
                if (prev != null && prev.val > cur.val) {
                    if (first == null) first = prev;
                    second = cur;
                }
                prev = cur;
                cur = cur.right;
            } else {
                // Find in-order predecessor: rightmost node of left subtree,
                // stopping if we already threaded back to `cur`.
                TreeNode pred = cur.left;
                while (pred.right != null && pred.right != cur) pred = pred.right;

                if (pred.right == null) {
                    // First time: thread predecessor.right -> cur, then descend left.
                    pred.right = cur;
                    cur = cur.left;
                } else {
                    // Second time: thread already exists, undo it, visit `cur`, go right.
                    pred.right = null;
                    if (prev != null && prev.val > cur.val) {
                        if (first == null) first = prev;
                        second = cur;
                    }
                    prev = cur;
                    cur = cur.right;
                }
            }
        }

        if (first != null && second != null) {
            int t = first.val; first.val = second.val; second.val = t;
        }
    }

    /* --------------------------- demo / tests --------------------------- */

    public static void main(String[] args) {
        RecoverBinarySearchTree solver = new RecoverBinarySearchTree();

        // Case A: adjacent swap. In-order [1, 3, 2] -> after fix [1, 2, 3].
        // Tree:   1
        //          \
        //           3
        //          /
        //         2
        TreeNode a = new TreeNode(1, null, new TreeNode(3, new TreeNode(2), null));
        solver.recoverTree(a);
        check("Case A (stack)", inorder(a), Arrays.asList(1, 2, 3));

        TreeNode aM = new TreeNode(1, null, new TreeNode(3, new TreeNode(2), null));
        solver.recoverTreeMorris(aM);
        check("Case A (morris)", inorder(aM), Arrays.asList(1, 2, 3));

        // Case B: non-adjacent swap. In-order [1, 3, 2, 4] -> after fix [1, 2, 3, 4].
        // Tree:     3
        //          / \
        //         1   4
        //              \
        //               2   (planted incorrectly; really should be 3)
        // Easier construction: take BST [1,2,3,4] and swap two non-adjacent.
        // Build [3,1,4,null,null,2,null]:
        //         3
        //        / \
        //       1   4
        //          /
        //         2
        TreeNode b = new TreeNode(3,
                new TreeNode(1),
                new TreeNode(4, new TreeNode(2), null));
        solver.recoverTree(b);
        check("Case B (stack)", inorder(b), Arrays.asList(1, 2, 3, 4));

        TreeNode bM = new TreeNode(3,
                new TreeNode(1),
                new TreeNode(4, new TreeNode(2), null));
        solver.recoverTreeMorris(bM);
        check("Case B (morris)", inorder(bM), Arrays.asList(1, 2, 3, 4));

        // Single node: no-op.
        TreeNode c = new TreeNode(42);
        solver.recoverTree(c);
        check("single node", inorder(c), Arrays.asList(42));

        // Root + leaves swapped (root with leftmost leaf).
        // Correct BST [1,2,3]:  2 / 1 \ 3.  Swap 1 and 2 -> tree (2,1,3) becomes (1,2,3).
        TreeNode d = new TreeNode(1, new TreeNode(2), new TreeNode(3));
        solver.recoverTreeMorris(d);
        check("root-leaf (morris)", inorder(d), Arrays.asList(1, 2, 3));
    }

    private static List<Integer> inorder(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) { stack.push(cur); cur = cur.left; }
            cur = stack.pop();
            out.add(cur.val);
            cur = cur.right;
        }
        return out;
    }

    private static void check(String label, List<Integer> got, List<Integer> expected) {
        boolean ok = got.equals(expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + " expected=" + expected + " got=" + got);
    }
}
