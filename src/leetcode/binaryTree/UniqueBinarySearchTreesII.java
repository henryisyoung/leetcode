package leetcode.binaryTree;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class UniqueBinarySearchTreesII {
    public List<TreeNode> generateTrees(int n) {
        List<TreeNode> result = new ArrayList<>();
        if (n == 0){
            return result;
        }
        return generateTreesHelper(1, n);
    }

    private List<TreeNode> generateTreesHelper(int start, int end) {
        List<TreeNode> result = new ArrayList<>();
        if (start > end) {
            result.add(null);
            return result;
        }
        for (int i = start; i <= end; i++) {
            List<TreeNode> left = generateTreesHelper(start, i - 1);
            List<TreeNode> right = generateTreesHelper(i + 1, end);
            for (TreeNode l : left) {
                for (TreeNode r : right) {
                    TreeNode root = new TreeNode(i);
                    root.left = l;
                    root.right = r;
                    result.add(root);
                }
            }
        }
        return result;
    }
}
