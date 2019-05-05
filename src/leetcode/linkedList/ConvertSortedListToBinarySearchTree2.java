//package leetcode.linkedList;
//
//import Bloomberg.TreeNode;
//
//public class ConvertSortedListToBinarySearchTree2 {
//    ListNode node;
//    public TreeNode sortedListToBST(ListNode head) {
//        this.node = head;
//        int size = findSize(head);
//        return sortListHelper(0, size - 1);
//    }
//
//    private TreeNode sortListHelper(int start, int end) {
//        if (start > end) {
//            return null;
//        }
//        int mid = start + (end - start) / 2;
//        TreeNode left = sortListHelper(start, mid - 1);
//        TreeNode root = new TreeNode(node.val);
//        root.left = left;
//        node = node.next;
//        TreeNode right = sortListHelper(mid + 1, end);
//        root.right = right;
//        return root;
//    }
//
//    private int findSize(ListNode head) {
//        ListNode n = head;
//        int count = 0;
//        while (n != null) {
//            n = n.next;
//            count++;
//        }
//        return count;
//    }
//}
