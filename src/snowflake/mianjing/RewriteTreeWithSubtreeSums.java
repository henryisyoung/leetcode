package snowflake.mianjing;

import Bloomberg.TreeNode;

/*
You are given two complete binary trees named root1 and root2.

These two trees are identical in shape. They have the exact same structure and the same number of nodes.

Your task is to update the values inside root2. For every node in root2, you must calculate the sum of the matching subtree in root1. This sum should include the node in root1 itself and all of its descendants (children, grandchildren, etc.).

After updating all the values, return the modified root2.

Sample Cases
Example 1

Input: root1 = [5,2,3,1,4,6,7], root2 = [9,9,9,9,9,9,9]
Output: [28,7,16,1,4,6,7]
Explanation:

To find the new values for root2, we calculate the subtree sums from root1:

Leaf Nodes: The nodes at the bottom (1, 4, 6, 7) have no children. Their subtree sum is just their own value.
Node 2: This node has children 1 and 4. The sum is 2 + 1 + 4 = 7.
Node 3: This node has children 6 and 7. The sum is 3 + 6 + 7 = 16.
Root Node 5: This node includes the sums from the left side (7) and the right side (16). The total is 5 + 7 + 16 = 28.
Example 2

Input: root1 = [1,2,3,4,5,6], root2 = [0,0,0,0,0,0]
Output: [21,11,9,4,5,6]
Input Limitations
Tree Size: The number of nodes is between 1 and 100,000 ($10^5$).
Node Values: The value of each node is between -10,000 and 10,000.
Tree Type: Both root1 and root2 are complete binary trees.
Structure: root1 and root2 have the exact same shape.
 */
public class RewriteTreeWithSubtreeSums {
    public void rewrite(TreeNode root1, TreeNode root2) {
        if (root1 == null) return;
        dfsUpdate(root1, root2);
    }

    private int dfsUpdate(TreeNode root1, TreeNode root2) {
        if (root1 == null) {
            return 0;
        }
        int left = dfsUpdate(root1.left, root2.left);
        int right = dfsUpdate(root1.right, root2.right);
        int sum = root1.val + left + right;
        root2.val = sum;
        return sum;
    }
}
