package leetcode.binaryTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BinaryTreeRightSideView2 {


    public List<Integer> rightSideViewBfs(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if(root == null) {
            return result;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode n = queue.poll();
                if (i == size - 1) {
                    result.add(n.val);
                }
                if (n.left != null) {
                    queue.add(n.left);
                }
                if (n.right != null) {
                    queue.add(n.right);
                }
            }
        }

        return result;
    }
    int maxLevel = 0;
    public List<Integer> rightSideView2(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        findRightMost(root, 1, list);
        return list;
    }

    private void findRightMost(TreeNode root, int i, List<Integer> list) {
        if(root == null) {
            return;
        }
        if(i > maxLevel) {
            maxLevel = i;
            list.add(root.val);
        }
        findRightMost(root.right, i + 1, list);
        findRightMost(root.left, i + 1, list);
    }

    public List<Integer> rightSideView3(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
//        list.add(root.val);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode cur = queue.poll();
                if(i == size - 1) {
                    list.add(cur.val);
                }
                if(cur.left != null) {
                    queue.add(cur.left);
                }
                if(cur.right != null) {
                    queue.add(cur.right);
                }
            }
        }
        return list;
    }
}
