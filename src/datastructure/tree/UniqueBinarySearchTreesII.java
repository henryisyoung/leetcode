package datastructure.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UniqueBinarySearchTreesII {
    public List<TreeNode> generateTrees(int n) {
        return buildTrees(1, n);
    }

    private List<TreeNode> buildTrees(int l, int r) {
        if (l > r)  {
            TreeNode node = null;
            return Arrays.asList(node);
        }
        if (l == r) {
            return Arrays.asList(new TreeNode(l));
        }
        List<TreeNode> result = new ArrayList<>();
        for (int mid = l; mid <= r; mid++) {
            List<TreeNode> leftNodes = buildTrees(l, mid - 1);
            List<TreeNode> rightNodes = buildTrees(mid + 1, r);
            for (TreeNode left : leftNodes) {
                for (TreeNode right : rightNodes) {
                    TreeNode cur = new TreeNode(mid);
                    cur.left = left;
                    cur.right = right;
                    result.add(cur);
                }
            }
        }
        return result;
    }
}
