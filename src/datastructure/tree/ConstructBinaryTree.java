package datastructure.tree;

public class ConstructBinaryTree {
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        return helper(preorder, 0, preorder.length - 1, inorder, 0 , inorder.length - 1);
    }

    private TreeNode helper(int[] preorder, int pl, int pr, int[] inorder, int il, int ir) {
        if (pl > pr) return null;
        if (pl == pr) {
            return new TreeNode(preorder[pl]);
        }

        int val = preorder[pl];
        int count = 0;
        while (inorder[il + count] != val) count++;
        TreeNode cur = new TreeNode(val);
        TreeNode left = helper(preorder, pl + 1, pl + count, inorder, il, il + count - 1);
        TreeNode right = helper(preorder, pl + count + 1, pr, inorder, il + count + 1, ir);
        cur.left = left;
        cur.right = right;
        return cur;
    }
}
