package snowflake.mianjing;

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
public class TreeLevelsAfterNodeDeletions {
    int maxDepth = 0;
    Set<Integer> del;
    public int treeLevelsAfterDeletions(TreeNode root, int[] toDelete) {
        del = new HashSet<>();
        for (int x : toDelete) del.add(x);
        dfs(root, true);
        return maxDepth;
    }

    //the height of the tallest surviving chain that starts at node and goes down, measured in the resulting forest.
    private int dfs(TreeNode node, boolean parentGoneOrRoot) {
        if (node == null) return 0;
        boolean deleted = del.contains(node.val);
        int left  = dfs(node.left,  deleted);
        int right = dfs(node.right, deleted);
        if (deleted) {
            return Math.max(left, right);
        }
        int depthHere = 1 + Math.max(left, right);
        if (parentGoneOrRoot) {
            maxDepth = Math.max(maxDepth, depthHere);
        }
        return depthHere;
    }


    // t2: Given a binary tree and an integer N, delete the minimum number of nodes so that the resulting forest has maximum depth ≤ N.
    // f(v, k) = maximum number of nodes we can KEEP in the subtree rooted at v, given that the path from the original root down to v's parent already contains k kept ancestors.
    int N;
    Map<TreeNode, int[]> memo = new HashMap<>();
    public int minDeletions(TreeNode root, int N) {
        this.N = N;
        int total = count(root);
        int kept = dfs(root, 0);
        return total - kept;
    }

    private int dfs(TreeNode v, int k) {
        if (v == null) return 0;
        if (k >= N) return 0;
        int[] cache = memo.get(v);
        if (cache == null) {
            cache = new int[N + 1];
            Arrays.fill(cache, -1);
            memo.put(v, cache);
        }
        if (cache[k] != -1) return cache[k];
        int delete = dfs(v.left, k) + dfs(v.right, k);
        int keep   = 1 + dfs(v.left, k + 1) + dfs(v.right, k + 1);
        return cache[k] = Math.max(delete, keep);
    }

    private int count(TreeNode v) {
        return v == null ? 0 : 1 + count(v.left) + count(v.right);
    }

    // t3: Among all solutions that delete the FEWEST nodes (so max depth <= N),
    //     also maximize the sum of ORIGINAL depths of the deleted nodes.
    //     Returns long[]{ minDeletions, maxDeletedDepthSum }.
    //
    // Idea: extend t2's DP from a single value (maxKept) to a lex-ordered pair
    //       (maxKept, maxDeletedDepthSum). Compare by kept first; on a tie,
    //       break by deletedDepthSum.
    //
    // State: dfs3(v, k, depth) -> long[]{ keptInSubtree, deletedDepthSumInSubtree }
    //   v     = current node
    //   k     = # kept ancestors on path from root to v's parent
    //   depth = original depth of v in the input tree (root depth = 1)
    // Memo key is (v, k); depth is a fixed property of v, so it does not
    // need to be part of the cache key.
    Map<TreeNode, long[][]> memo3;

    public long[] minDeletionsMaxDeletedDepthSum(TreeNode root, int N) {
        this.N = N;
        this.memo3 = new HashMap<>();
        long[] best = dfs3(root, 0, 1);
        long total = count(root);
        return new long[]{ total - best[0], best[1] };
    }

    private long[] dfs3(TreeNode v, int k, int depth) {
        if (v == null) return new long[]{ 0, 0 };

        long[][] cache = memo3.get(v);
        if (cache == null) {
            cache = new long[N + 1][];
            memo3.put(v, cache);
        }
        if (cache[k] != null) return cache[k];

        // Option A: delete v. Children float up, parent's k unchanged.
        // The deleted-depth-sum gains `depth` (v itself) plus the sums from children.
        long[] lDel = dfs3(v.left,  k, depth + 1);
        long[] rDel = dfs3(v.right, k, depth + 1);
        long[] delete = { lDel[0] + rDel[0], depth + lDel[1] + rDel[1] };

        long[] result;
        if (k >= N) {
            // Keeping v would put it at level k+1 > N → forced to delete.
            result = delete;
        } else {
            // Option B: keep v. Consumes one slot of the depth budget.
            long[] lKeep = dfs3(v.left,  k + 1, depth + 1);
            long[] rKeep = dfs3(v.right, k + 1, depth + 1);
            long[] keep  = { 1 + lKeep[0] + rKeep[0], lKeep[1] + rKeep[1] };
            result = better(keep, delete);
        }
        return cache[k] = result;
    }

    // Lex order: prefer larger kept; break ties by larger deletedDepthSum.
    private long[] better(long[] a, long[] b) {
        if (a[0] != b[0]) return a[0] > b[0] ? a : b;
        return a[1] >= b[1] ? a : b;
    }
}
