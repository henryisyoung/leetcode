package airbnb.New2026;
/*
House Robber III  (LC 337).

A thief plans to rob houses arranged in a BINARY TREE. The root is the
entrance; each child is a neighboring house. The constraint: it will
automatically alert the police if TWO DIRECTLY-LINKED houses (parent
and child) are robbed on the same night.

Given the tree's root, return the maximum amount of money the thief
can rob without alerting the police.

I/O
  Input : root (TreeNode)
  Output: int (max amount; >= 0)

Constraints
  0 <= nodes <= 1e4
  0 <= node.val <= 1e4

Examples
  Tree    3
         / \
        2   3       -> rob {3, 3, 1} = 7
         \   \
          3   1

  Tree    3
         / \
        4   5       -> rob {4, 5} = 9 (skip the 3, take both kids)
       / \   \
      1   3   1
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/*
Algorithm: post-order DP that returns TWO numbers per subtree.

  For each node, compute pair (notRob, rob):
     notRob = best total for this subtree if THIS node is NOT robbed
            = max(child.notRob, child.rob) summed over children
              (each child is independently free to rob or skip)
     rob    = best total for this subtree if THIS node IS robbed
            = node.val + child.notRob summed over children
              (forced: children cannot also be robbed)

  Answer = max(root.notRob, root.rob).

  Why this beats memoised "rob vs. skip" recursion:
    The naive "rob root or skip root" branch recomputes the same
    grandchildren multiple times. Memoising on (node, parentRobbed)
    works but allocates a map; the pair-return trick removes the
    map entirely — one post-order pass, O(1) work per node.

  Iterative variant:
    Recursion depth could be the tree height. For a worst-case
    skewed tree of 1e4 nodes, the default JVM stack (~256–512 KB)
    typically tolerates that, but to be safe we also provide an
    explicit-stack post-order via a HashMap<TreeNode,int[]> keyed
    on the node identity.  That keeps us safe up to whatever
    `nodes` the heap can hold.

Complexity
  Time:   O(n)
  Memory: O(h) recursion stack (or O(n) for the iterative variant).
*/
public class HouseRobberIII {

    public static class TreeNode {
        public int val;
        public TreeNode left, right;
        public TreeNode(int val) { this.val = val; }
        public TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val; this.left = left; this.right = right;
        }
    }

    /** Recursive post-order. Clearest version; safe for typical heights. */
    public int rob(TreeNode root) {
        int[] r = dfs(root);
        return Math.max(r[0], r[1]);
    }

    private int[] dfs(TreeNode node) {
        if (node == null) return new int[]{0, 0};
        int[] L = dfs(node.left);
        int[] R = dfs(node.right);
        int notRob = Math.max(L[0], L[1]) + Math.max(R[0], R[1]);
        int rob    = node.val + L[0] + R[0];
        return new int[]{notRob, rob};
    }

    /** Iterative post-order using a two-pass deque — robust against very
     *  deep / skewed trees that could otherwise blow the JVM stack. */
    public int robIterative(TreeNode root) {
        if (root == null) return 0;

        // Post-order via the "reverse pre-order" trick.
        Deque<TreeNode> stack = new ArrayDeque<>();
        Deque<TreeNode> order = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode n = stack.pop();
            order.push(n);
            if (n.left  != null) stack.push(n.left);
            if (n.right != null) stack.push(n.right);
        }
        // Identity map by node — TreeNode has no built-in id, so use
        // an IdentityHashMap-style lookup via a small wrapper array.
        java.util.IdentityHashMap<TreeNode, int[]> dp = new java.util.IdentityHashMap<>();
        while (!order.isEmpty()) {
            TreeNode n = order.pop();
            int[] L = n.left  == null ? new int[]{0, 0} : dp.get(n.left);
            int[] R = n.right == null ? new int[]{0, 0} : dp.get(n.right);
            int notRob = Math.max(L[0], L[1]) + Math.max(R[0], R[1]);
            int rob    = n.val + L[0] + R[0];
            dp.put(n, new int[]{notRob, rob});
        }
        int[] r = dp.get(root);
        return Math.max(r[0], r[1]);
    }

    /* --------------------------- IO + demo --------------------------- */

    public static void main(String[] args) throws IOException {
        if (args.length == 0 && hasStdin()) {
            runFromStdin();
            return;
        }
        runDemos();
    }

    private static boolean hasStdin() {
        try { return System.in.available() > 0; } catch (IOException e) { return false; }
    }

    /**
     * Stdin format: one line, a space-separated LEVEL-ORDER serialisation
     * using "null" for missing children, e.g.
     *   3 2 3 null 3 null 1
     *   3 4 5 1 3 null 1
     */
    private static void runFromStdin() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        HouseRobberIII solver = new HouseRobberIII();
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) { System.out.println(0); continue; }
            TreeNode root = parseLevelOrder(line);
            System.out.println(solver.rob(root));
        }
    }

    private static void runDemos() {
        HouseRobberIII solver = new HouseRobberIII();

        //     3
        //    / \
        //   2   3
        //    \   \
        //     3   1     -> 7
        TreeNode t1 = parseLevelOrder("3 2 3 null 3 null 1");
        check("ex1", solver.rob(t1), 7);
        check("ex1 iter", solver.robIterative(t1), 7);

        //     3
        //    / \
        //   4   5
        //  / \   \
        // 1   3   1     -> 9
        TreeNode t2 = parseLevelOrder("3 4 5 1 3 null 1");
        check("ex2", solver.rob(t2), 9);
        check("ex2 iter", solver.robIterative(t2), 9);

        // ---- Edge cases ----
        check("empty",    solver.rob(null), 0);
        check("single",   solver.rob(new TreeNode(7)), 7);
        check("zero val", solver.rob(new TreeNode(0)), 0);

        //   1
        //    \
        //     2          -> max(1, 2) = 2 (skip parent, take child)
        TreeNode chain2 = new TreeNode(1, null, new TreeNode(2));
        check("chain-2", solver.rob(chain2), 2);

        //   1 - 2 - 3 - 4   right-chain only
        //   Levels: 1,2,3,4 — adjacency forbids taking neighbours.
        //   Best = 1 + 3 = 4, or 2 + 4 = 6. Answer: 6.
        TreeNode chain4 = new TreeNode(1, null,
                new TreeNode(2, null,
                        new TreeNode(3, null, new TreeNode(4))));
        check("chain-4", solver.rob(chain4), 6);

        // All-zero tree of moderate depth.
        TreeNode zeros = parseLevelOrder("0 0 0 0 0 0 0");
        check("all-zero", solver.rob(zeros), 0);

        // Iterative agrees with recursive on a random tree.
        TreeNode rnd = parseLevelOrder("5 1 8 null 2 6 9 null null 3 7");
        check("random recursive == iterative",
                solver.rob(rnd), solver.robIterative(rnd));

        // ---- Stress: long skewed chain (forces deepest recursion). ----
        int N = 10_000;
        TreeNode head = new TreeNode(1);
        TreeNode cur = head;
        for (int i = 2; i <= N; i++) {
            cur.right = new TreeNode(1);
            cur = cur.right;
        }
        // Optimal on a 1-1-1-...-1 chain of N nodes = ceil(N/2).
        int expected = (N + 1) / 2;
        long t0 = System.nanoTime();
        int got = solver.robIterative(head);                  // recursive may stack-overflow on really deep chains
        long ms = (System.nanoTime() - t0) / 1_000_000;
        check("stress skewed N=" + N, got, expected);
        System.out.println("Stress n=" + N + " in " + ms + " ms");
    }

    /* --------------------------- helpers --------------------------- */

    /** Build a binary tree from LeetCode-style level-order tokens. */
    static TreeNode parseLevelOrder(String s) {
        if (s == null) return null;
        String[] tok = s.trim().split("\\s+");
        if (tok.length == 0 || tok[0].isEmpty() || tok[0].equals("null")) return null;
        TreeNode root = new TreeNode(Integer.parseInt(tok[0]));
        Deque<TreeNode> q = new ArrayDeque<>();
        q.offer(root);
        int i = 1;
        while (!q.isEmpty() && i < tok.length) {
            TreeNode n = q.poll();
            if (i < tok.length && !tok[i].equals("null")) {
                n.left = new TreeNode(Integer.parseInt(tok[i]));
                q.offer(n.left);
            }
            i++;
            if (i < tok.length && !tok[i].equals("null")) {
                n.right = new TreeNode(Integer.parseInt(tok[i]));
                q.offer(n.right);
            }
            i++;
        }
        return root;
    }

    private static void check(String label, int got, int expected) {
        boolean ok = got == expected;
        System.out.println((ok ? "OK   " : "FAIL ") + label + "  got=" + got + " expected=" + expected);
    }
}
