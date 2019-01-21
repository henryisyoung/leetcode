package leetcode;

public class Solution106 {
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
		while(count <= ir - il && postorder[pr] != inorder[il + count]) count++;
		TreeNode root = new TreeNode(postorder[pr]);
		
		TreeNode left = buildTreeHelper(postorder,pl ,pl + count - 1,inorder,il,il + count -1);
		TreeNode right = buildTreeHelper(postorder,pl + count,pr - 1,inorder,il + count + 1,ir);
		root.left = left;
		root.right = right;
		return root;
	}
}
