package LinkedIn;

import Bloomberg.TreeNode;

public class LcaOfBst {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root.val == p.val || root.val == q.val) return root;
        if (root.val < Math.max(p.val, q.val) && root.val > Math.min(p.val, q.val)) {
            return root;
        }
        if (root.val > p.val) return lowestCommonAncestor(root.left, p, q);
        return lowestCommonAncestor(root.right, p , q);
    }
}
