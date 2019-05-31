package leetcode.binaryTree;

import java.util.TreeSet;

public class CountCompleteTreeNodes {
    public int countNodes(TreeNode root) {
        if(root == null){
            return 0;
        }
        int left = findLeftHeight(root.left);
        int right = findRightHeight(root.right);
        if(left == right) {
            return (2 << left) - 1;
        }
        return countNodes(root.left) + countNodes(root.right) + 1;
    }

    private int findRightHeight(TreeNode node) {
        int level = 0;
        while (node != null){
            level++;
            node = node.right;
        }
        return level;
    }

    private int findLeftHeight(TreeNode node) {
        int level = 0;
        while (node != null){
            level++;
            node = node.left;
        }
        return level;
    }

    public int countNodes2(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int leftHeight = 0, rightHeight = 0;
        TreeNode n = root;
        while (n != null) {
            leftHeight++;
            n = n.left;
        }
        n = root;
        while (n != null) {
            rightHeight++;
            n = n.right;
        }
        if (leftHeight == rightHeight) {
            return (1 << leftHeight) - 1;
        }
        return 1 + countNodes2(root.left) + countNodes2(root.right);
    }
}
