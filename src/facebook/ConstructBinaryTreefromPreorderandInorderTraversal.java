package facebook;

import Bloomberg.TreeNode;

public class ConstructBinaryTreefromPreorderandInorderTraversal {
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        if (preorder.length != inorder.length) return null;
        return buildTreehelper(preorder, 0, preorder.length - 1, inorder, 0, inorder.length - 1);
    }

    private TreeNode buildTreehelper(int[] preorder, int pl, int pr,
                                     int[] inorder, int il, int ir) {
        if (pl > pr) return null;
        int val = preorder[pl];
        int count = 0;
        while (inorder[count + il] != val) count++;
        TreeNode root = new TreeNode(val);
        TreeNode left = buildTreehelper(preorder, pl + 1, pl + count, inorder, il, il + count - 1);
        TreeNode right = buildTreehelper(preorder, pl + count + 1, pr, inorder,il + count + 1, ir);
        root.left = left;
        root.right = right;

        return root;
    }
}
