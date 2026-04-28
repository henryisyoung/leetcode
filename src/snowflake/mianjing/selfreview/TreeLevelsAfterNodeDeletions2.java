package snowflake.mianjing.selfreview;

import Bloomberg.TreeNode;

import java.util.*;

/*
Problem Overview
You are given the root of a binary tree. Every node in this tree has a unique ID.
You are also given a list called toDelete that contains the IDs of nodes you must remove.

Here is how the deletion works:

When a node is deleted, it is taken out of the tree.
Its children move up to replace it. They attach to the parent of the deleted node.
If you delete several nodes in a row, the descendants keep moving up until they find a node that is not deleted.
If no non-deleted parent exists, those descendants become new roots.
After applying all deletions, you might end up with a forest (a collection of separate trees).
Your goal is to find the maximum depth (number of levels) in this final forest.

A single root counts as 1 level.
If every node is deleted, return 0.
Sample Cases
Example 1:

Input: root = [1,2,3,4,5,null,6], toDelete = [2]

Output: 3

Explanation: You delete node 2. Its children (4 and 5) move up to the parent level. The longest path remaining in the tree is 1 -> 3 -> 6.
This path has 3 levels.

Example 2:

Input: root = [1,2,3,4,null,null,5], toDelete = [1,3]

Output: 2

Explanation: You delete nodes 1 and 3. This splits the tree. Node 2 (which still has child 4) becomes a root. Node 5 also becomes a root.
The tallest tree has 2 levels.

Input Limits
Number of nodes is between 1 and 10^5.
Node.val is between -10^5 and 10^5.
All Node.val entries are unique.
The length of toDelete is between 0 and the total number of nodes.
The IDs in toDelete are valid IDs found in the tree.

t1 一棵树 删一些点给最大深度
t2 求删除最少点让最大深度不超过N - Remove the minimum number of nodes so that the maximum depth does not exceed N.
t3 做少点之上 还要让删除点深度和最大
 */
public class TreeLevelsAfterNodeDeletions2 {

    int max;

    public int treeLevelsAfterDeletions(TreeNode root, int[] toDelete) {
        if (root == null) {
            return 0;
        }
        Set<Integer> set = new HashSet<>();
        for (int del : toDelete) set.add(del);
        max = 0;
        dfsFindMax(set, root);
        return max;
    }

    private int dfsFindMax(Set<Integer> set, TreeNode root) {
        if (root == null) {
            return 0;
        }
        boolean deleted = set.contains(root.val);
        int left = dfsFindMax(set, root.left);
        int right = dfsFindMax(set, root.right);
        if (deleted) {
            return Math.max(left, right);
        }

        int depth = 1 + Math.max(left, right);
        max = Math.max(depth, max);
        return depth;
    }

    Map<TreeNode, int[]> memo;
    public int minDeletions(TreeNode root, int N) {
        if (root == null) {
            return 0;
        }
        this.memo = new HashMap<>();
        int total = countAll(root);
        int maxHold = dfsMaxH(root, 0, N);
        return total - maxHold;
    }

    private int dfsMaxH(TreeNode node, int k, int n) {
        if (node == null) {
            return 0;
        }
        if (k == n) {
            return 0;
        }
        int[] dp = memo.get(node);
        if (dp == null) {
            dp = new int[n + 1];
            Arrays.fill(dp, -1);
            memo.put(node, dp);
        }
        if (dp[k] != -1) {
            return dp[k];
        }
        int left = dfsMaxH(node.left, k, n);
        int right = dfsMaxH(node.right, k, n);
        int delete =left + right;
        int hold = 1 + dfsMaxH(node.left, k + 1, n) + dfsMaxH(node.right, k + 1, n);
        dp[k] = Math.max(delete, hold);
        return Math.max(delete, hold);
    }

    private int countAll(TreeNode root) {
        if(root == null) return 0;
        return 1 + countAll(root.left) + countAll(root.right);
    }
}