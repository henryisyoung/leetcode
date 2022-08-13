package rippling;

import Bloomberg.TreeNode;

public class HouseRobberIII {
    public int rob(TreeNode root) {
        if (root == null) {
            return 0;
        }
        return findAll(root)[1];
    }

    private int[] findAll(TreeNode root) {
        if (root == null) {
            return new int[]{0, 0};
        }
        int[] left = findAll(root.left);
        int[] right = findAll(root.right);
        int[] cur = new int[2];
        // index 0 is - not steal cur level
        cur[0] = left[1] + right[1];
        // index 1 is global max
        cur[1] = Math.max(cur[0], root.val + left[0] + right[0]);
        return cur;
    }
}
