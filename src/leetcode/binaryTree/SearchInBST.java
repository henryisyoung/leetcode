package leetcode.binaryTree;

public class SearchInBST {
    public TreeNode searchBST(TreeNode root, int val) {
        if(root == null){
            return null;
        }
        TreeNode cur = root;
        while (cur != null){
            if(cur.val == val) {
                return cur;
            } else if (cur.val > val) {
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }
        return null;
    }
}
