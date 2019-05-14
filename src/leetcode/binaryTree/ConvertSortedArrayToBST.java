package leetcode.binaryTree;

public class ConvertSortedArrayToBST {
    public TreeNode sortedArrayToBST(int[] nums) {
        if (nums == null || nums.length == 0) {
            return null;
        }
        return sortedArrayToBSTHelper(nums, 0, nums.length - 1);
    }

    private TreeNode sortedArrayToBSTHelper(int[] nums, int s, int e) {
        if(s > e){
            return null;
        }
        if (s == e) {
            return new TreeNode(nums[s]);
        }
        int mid = s + (e - s) / 2;
        TreeNode root = new TreeNode(nums[mid]);
        TreeNode left = sortedArrayToBSTHelper(nums, s, mid - 1);
        TreeNode right = sortedArrayToBSTHelper(nums, mid + 1, e);
        root.left = left;
        root.right = right;
        return root;
    }
}
