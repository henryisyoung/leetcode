package facebook;

import Bloomberg.TreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//https://www.geeksforgeeks.org/print-longest-leaf-leaf-path-binary-tree/
public class _DiameterBinaryTree {
    int max = 0;
    public int diameterOfBinaryTree(TreeNode root) {
        if (root == null) return 0;
        helper(root);
        return max;
    }

    private int helper(TreeNode root) {
        if (root == null) return 0;
        int left = helper(root.left), right = helper(root.right);
        max = Math.max(left + right + 1, max);
        return Math.max(left, right) + 1;
    }

    TreeNode maxNode;
    int maxLeft, maxRight, longest;
    public List<Integer> diameterOfBinaryTree2(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        this.maxNode = null;
        this.maxLeft = 0;
        this.maxRight = 0;
        this.longest = 0;
        helper2(root);

        List<Integer> leftPath = new ArrayList<>(), rightPath = new ArrayList<>();
        TreeNode node = maxNode;

        findPath(leftPath,  node.left, new ArrayList<>());
        findPath(rightPath, node.right, new ArrayList<>());
        Collections.reverse(leftPath);
        result.addAll(leftPath);
        result.add(maxNode.val);
        result.addAll(rightPath);
        return result;
    }

    private void findPath(List<Integer> result, TreeNode node, List<Integer> list) {
        if (node == null) {
            return;
        }
        list.add(node.val);

        if (node.left == null && node.right == null) {
            if (list.size() > result.size()) {
                result.clear();
                result.addAll(new ArrayList<>(list));
            }
            list.remove(list.size() - 1);
            return;
        }

        findPath(result, node.left, list);
        findPath(result, node.right, list);
        list.remove(list.size() - 1);
    }

    private int helper2(TreeNode root) {
        if (root == null) return 0;
        int left = helper(root.left), right = helper(root.right);
        if (left + right + 1 > longest) {
            longest = left + right + 1;
            maxLeft = left;
            maxRight = right;
            maxNode = root;
        }

        return Math.max(left, right) + 1;
    }

    public static void main(String[] args) {
        //           1
        //         /   \
        //        2     3
        //      /   \   /
        //     4     5  31
        //      \   / \
        //       8 6   7
        //      /
        //     9
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.right.left = new TreeNode(31);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.left.right.left = new TreeNode(6);
        root.left.right.right = new TreeNode(7);
        root.left.left.right = new TreeNode(8);
        root.left.left.right.left = new TreeNode(9);
        _DiameterBinaryTree solution = new _DiameterBinaryTree();
        System.out.println(solution.diameterOfBinaryTree2(root));
    }
}
