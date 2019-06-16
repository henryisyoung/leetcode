package leetcode.solution;

public class Question450_Binary_Search_Tree {
    public TreeNode deleteNode(TreeNode root, int key) {
        if(root == null) return root;
        if(root.val > key){
            root.left = deleteNode(root.left, key);
        }else if(root.val < key){
            root.right = deleteNode(root.right, key);
        }else {
            if(root.right == null) return root.left;
            else if(root.left == null) return root.right;
            else {
                root.val = find_rightmost(root.left);
                root.left = deleteNode(root.left, root.val);
            }
        }
        return root;
    }

    private int find_rightmost(TreeNode p) {
        while (p.right != null){
            p = p.right;
        }
        return p.val;
    }
}
