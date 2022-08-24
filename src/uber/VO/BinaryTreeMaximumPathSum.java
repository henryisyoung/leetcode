//package uber.VO;
//
//import Bloomberg.TreeNode;
//
//public class BinaryTreeMaximumPathSum {
//    public int maxPathSum(TreeNode root) {
//        if (root == null) {
//            return 0;
//        }
//        int[] result = maxPathSumHelper(root);
//        return result[1];
//    }
//
//    private int[] maxPathSumHelper(TreeNode root) {
//        if (root == null) {
//            return new int[]{0, Integer.MIN_VALUE};
//        }
//        int[] left = maxPathSumHelper(root.left);
//        int[] right = maxPathSumHelper(root.right);
//        int
//    }
//}
