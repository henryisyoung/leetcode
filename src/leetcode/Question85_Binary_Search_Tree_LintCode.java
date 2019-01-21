package leetcode;

public class Question85_Binary_Search_Tree_LintCode {
    public TreeNode insertNode(TreeNode root, TreeNode node) {
        return helper(root, node.val);
    }

    private TreeNode helper(TreeNode root, int val) {
        if(root == null) return new TreeNode(val);
        if(root.val == val) return root;
        if(root.val > val) {
            root.left = helper(root.left, val);
            return root;
        }
        root.right = helper(root.right, val);
        return root;
    }
}
