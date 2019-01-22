package leetcode.binaryTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BinaryTreeRightSideView {
    int maxDepth = 0;
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if(root == null) {
            return result;
        }
        rightViewHelper(root, result, 1);
        return result;
    }

    private void rightViewHelper(TreeNode root, List<Integer> result, int depth) {
        if(depth > maxDepth){
            result.add(root.val);
            maxDepth = depth;
        }
        if(root.right != null){
            rightViewHelper(root.right, result, depth + 1);
        }
        if(root.left != null){
            rightViewHelper(root.left, result, depth + 1);
        }
    }

    public List<Integer> rightSideViewBfs(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if(root == null) {
            return result;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()){
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (i == size - 1) {
                    result.add(node.val);
                }
                if(node.left != null){
                    queue.add(node.left);
                }
                if(node.right != null){
                    queue.add(node.right);
                }
            }
        }

        return result;
    }
}
