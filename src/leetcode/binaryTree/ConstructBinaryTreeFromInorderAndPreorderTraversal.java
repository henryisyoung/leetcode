package leetcode.binaryTree;

public class ConstructBinaryTreeFromInorderAndPreorderTraversal {
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        if(preorder == null || preorder.length == 0) return null;
        if(inorder == null || inorder.length == 0) return null;
        if(preorder.length != inorder.length ) return null;
        return buildTreeHelper(preorder,0,preorder.length-1, inorder,0,preorder.length-1);
    }

    private TreeNode buildTreeHelper(int[] preorder, int pl, int pr,
                                     int[] inorder, int il, int ir) {
        if(pl > pr) return null;
        int count = 0;
        while(preorder[pl] != inorder[il + count]) count++;
        TreeNode root = new TreeNode(preorder[pl]);

        TreeNode left = buildTreeHelper(preorder,pl + 1,pl + count,inorder,il,il + count -1);
        TreeNode right = buildTreeHelper(preorder,pl + count + 1,pr,inorder,il + count + 1,ir);
        root.left = left;
        root.right = right;
        return root;
    }
}
