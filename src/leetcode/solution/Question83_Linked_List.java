package leetcode.solution;

public class Question83_Linked_List {
    public ListNode deleteDuplicates(ListNode head) {
        if(head == null || head.next == null) return head;
        ListNode cur = head;
        ListNode fast = head.next;
        while (fast != null){
            if(fast.val != cur.val) {
                cur.next = fast;
                cur = fast;
            }
            fast = fast.next;
        }
        cur.next = fast;
        return head;
    }
}
