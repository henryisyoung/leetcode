package datastructure.tree;

public class LowestCommonAncestorBST {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) return null;
        if ((p.val - root.val) * (q.val - root.val) <= 0) return root;

        if (p.val < root.val) {
            return lowestCommonAncestor(root.left, p ,q);
        }
        return lowestCommonAncestor(root.right, p ,q);
    }
}
