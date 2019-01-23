package leetcode.linkedList;

public class RemoveDuplicatesFromSortedListII {
    public ListNode deleteDuplicates(ListNode head) {
        if(head==null || head.next==null) return head;
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prev = dummy;
        ListNode cur = head;
        ListNode fast = cur.next;
        while (fast != null) {
            if(cur.val != fast.val){
                prev = cur;
                cur = fast;
                fast = fast.next;
            } else {
                while (fast != null && cur.val == fast.val) {
                    prev.next = fast;
                    fast = fast.next;
                }
                prev.next = fast;
                if (fast != null){
                    cur = fast;
                    fast = fast.next;
                }

            }
        }
        return dummy.next;
    }
}
