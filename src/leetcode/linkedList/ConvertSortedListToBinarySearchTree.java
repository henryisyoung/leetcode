//package leetcode.linkedList;
//
//import Bloomberg.TreeNode;
//
//public class ConvertSortedListToBinarySearchTree {
//    public TreeNode sortedListToBST(ListNode head) {
//        if(head == null) {
//            return null;
//        }
//        return sortedHelper(head, null);
//    }
//
//    private TreeNode sortedHelper(ListNode start, ListNode end) {
//        if(start == end){
//            return null;
//        }
//        ListNode mid = findMid(start, end);
//        TreeNode left = sortedHelper(start, mid);
//        TreeNode root = new TreeNode(mid.val);
//        TreeNode right = sortedHelper(mid.next, end);
//
//        root.left = left;
//        root.right = right;
//        return root;
//    }
//
//    private ListNode findMid(ListNode start, ListNode end) {
//        ListNode slow = start, fast = start.next;
//        while (fast != end && fast.next != end) {
//            fast = fast.next.next;
//            slow = slow.next;
//        }
//        return slow;
//    }
//}
