package leetcode.binaryTree;

public class ConstructBinaryTreeFromInorderAndPostorderTraversal {
    public TreeNode buildTree(int[] inorder, int[] postorder) {
        if(postorder == null || postorder.length == 0) return null;
        if(inorder == null || inorder.length == 0) return null;
        if(postorder.length != inorder.length ) return null;
        return buildTreeHelper(postorder,0,postorder.length-1,
                inorder,0,postorder.length-1);
    }

    private TreeNode buildTreeHelper(int[] postorder, int pl, int pr,
                                     int[] inorder, int il, int ir) {
        if(pl > pr) return null;
        int count = 0;
        while(postorder[pr] != inorder[il + count]) count++;
        TreeNode root = new TreeNode(postorder[pr]);

        TreeNode left = buildTreeHelper(postorder,pl ,pl + count - 1,inorder,il,il + count -1);
        TreeNode right = buildTreeHelper(postorder,pl + count,pr - 1,inorder,il + count + 1,ir);
        root.left = left;
        root.right = right;
        return root;
    }
    public TreeNode buildTree2(int[] inorder, int[] postorder) {
        if(postorder == null || postorder.length == 0) return null;
        if(inorder == null || inorder.length == 0) return null;
        if(postorder.length != inorder.length ) return null;
        int n = inorder.length;
        return treeBuildHelper(inorder, 0 , n - 1, postorder, 0, n - 1);
    }

    private TreeNode treeBuildHelper(int[] inorder, int iLeft, int iRight, int[] postorder, int pLeft, int pRight) {
        if (iLeft > iRight) {
            return null;
        }
        int count = 0;
        while (inorder[iLeft + count] != postorder[pRight]) {
            count++;
        }
        TreeNode root = new TreeNode(postorder[pRight]);
        TreeNode left = treeBuildHelper(inorder, iLeft, iLeft + count - 1, postorder, pLeft, pLeft + count - 1);
        TreeNode right = treeBuildHelper(inorder,iLeft + count + 1, iRight, postorder, pLeft + count , pRight - 1);
        root.left = left;
        root.right = right;
        return root;
    }

}
