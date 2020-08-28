package apple;

import java.util.ArrayList;
import java.util.List;

public class AllNodesDistanceKBinaryTree {
    
      public class TreeNode {
          int val;
          TreeNode left;
          TreeNode right;
          TreeNode(int x) { val = x; }
      }

    public List<Integer> distanceK(TreeNode root, TreeNode target, int K) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;
        findAll(result, root, target, K);
        return result;
    }

    private int findAll(List<Integer> result, TreeNode root, TreeNode target, int k) {
          if (root == null) return -1;
          if (root.val == target.val) {
              findBelow(k, result, root, 0);
              return 1;
          }
          int left = findAll(result, root.left, target, k);
          int right = findAll(result, root.right, target, k);
          if (left != -1) {
              if (left == k) {
                  result.add(root.val);
              }
              if (left < k) {
                  findBelow(k - left -1, result, root.right, 0);
              }
              return left + 1;
          }
          if (right != -1) {
              if (right == k) {
                  result.add(root.val);
              }
              if (right < k) {
                  findBelow(k - right - 1, result, root.left, 0);
              }
              return right + 1;
          }
          return -1;
    }

    private void findBelow(int k, List<Integer> result, TreeNode root, int level) {
          if (root == null) return;
          if (level == k) result.add(root.val);
          findBelow(k, result, root.left, level + 1);
          findBelow(k, result, root.right, level + 1);
    }
}
