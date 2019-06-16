package leetcode.solution;

public class Question109_Linked_List {
    ListNode cur;
    public TreeNode sortedListToBST(ListNode head) {
        if(head == null) return null;
        int size = 0;
        ListNode n = head;
        while (n != null){
            n = n.next;
            size++;
        }
        cur = head;
        return helper(size);
    }

    private TreeNode helper(int size) {
        if(size == 0){
            return null;
        }
        TreeNode left = helper(size/2);
        TreeNode root = new TreeNode(cur.val);
        cur = cur.next;
        TreeNode right = helper(size - size/2 - 1);
        root.left = left;
        root.right = right;
        return root;
    }

    public TreeNode sortedListToBST2(ListNode head) {
        if(head == null) return null;
        return sortedListHelper(head, null);
    }

    private TreeNode sortedListHelper(ListNode start, ListNode end) {
        if(start == end){
            return null;
        }
        ListNode mid = find_mid(start, end);
        TreeNode left = sortedListHelper(start, mid);
        TreeNode root = new TreeNode(mid.val);
        TreeNode right = sortedListHelper(mid.next, end);
        root.left = left;
        root.right = right;
        return root;
    }

    private ListNode find_mid(ListNode head, ListNode end) {
        ListNode slow = head, fast = head.next;
        while (fast != end && fast.next != end){
            fast = fast.next.next;
            slow = slow.next;
        }
        return slow;
    }
}
