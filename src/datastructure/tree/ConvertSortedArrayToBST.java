package datastructure.tree;

public class ConvertSortedArrayToBST {
    public TreeNode sortedArrayToBST(int[] nums) {
        if (nums == null || nums.length == 0) return null;
        int len = nums.length;

        return helper(nums, 0, len - 1);
    }

    private TreeNode helper(int[] nums, int l, int r) {
        if (l > r) return null;
        if (l == r) {
            return new TreeNode(nums[l]);
        }
        int mid = l + (r - l) / 2;
        TreeNode cur = new TreeNode(nums[mid]);
        TreeNode left = helper(nums, l, mid - 1);
        TreeNode right = helper(nums, mid + 1, r);
        cur.left = left;
        cur.right = right;

        return cur;
    }
}
