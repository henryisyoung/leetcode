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
        return countNodes(root.left) + countNodes(root.right);
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

    public static void main(String[] args) {
        System.out.println(2 << 0);
    }
}
