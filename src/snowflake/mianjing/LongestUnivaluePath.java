package snowflake.mianjing;

import Bloomberg.TreeNode;

import java.util.Map;

/*
Problem Explanation
You are provided with the root of a binary tree. Your task is to find the length of the longest path in the tree where every node in that path shares the exact same value.

Here are a few rules to keep in mind:

The path does not have to go through the root. It can start and end anywhere in the tree.
The length is measured by the number of edges (connections) between nodes, not the count of nodes themselves.
Test Cases
Case 1:

Input: root = [5,4,5,1,1,null,5]
Output: 2
Case 2:

Input: root = [1,4,5,4,4,null,5]
Output: 2
Input Limits
Tree Size: The number of nodes is between 0 and 10^4.
Node Values: Each Node.val is between -1000 and 1000.
 */
public class LongestUnivaluePath {
    private int max;

    public int findlongestPath(TreeNode root) {
        max = 0;                  // reset so multiple calls work
        arrow(root);
        return max;
    }

    // Returns the longest same-value "arrow" of EDGES going downward from node.
    // While computing it, also updates `max` for paths that bend through node.
    private int arrow(TreeNode node) {
        if (node == null) return 0;

        int leftArrow  = arrow(node.left);
        int rightArrow = arrow(node.right);

        int leftMatch  = (node.left  != null && node.left.val  == node.val) ? leftArrow  + 1 : 0;
        int rightMatch = (node.right != null && node.right.val == node.val) ? rightArrow + 1 : 0;

        // Path that bends through `node` uses BOTH sides.
        max = Math.max(max, leftMatch + rightMatch);

        // But what we hand back upward can only continue ONE side.
        return Math.max(leftMatch, rightMatch);
    }
}