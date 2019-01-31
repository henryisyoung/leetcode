package leetcode.binaryTree;

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
        if(root == null) {
            return 0;
        }
        int left = rootLeftHeight(root.left);
        int right = rootRightHeight(root.right);
        if(left == right) {
            return (2 << left) - 1;
        }
        return countNodes(root.left) + countNodes(root.right) + 1;
    }

    private int rootRightHeight(TreeNode right) {
        int level = 0;
        while (right != null) {
            right = right.right;
            level++;
        }
        return level;
    }

    private int rootLeftHeight(TreeNode left) {
        int level = 0;
        while (left != null) {
            left = left.left;
            level++;
        }
        return level;
    }
}
