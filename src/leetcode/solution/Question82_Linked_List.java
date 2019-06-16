package leetcode.solution;

public class Question82_Linked_List {
    public ListNode deleteDuplicates(ListNode head) {
        if(head == null || head.next == null) return head;
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode cur = head, prev = dummy, next = head.next;
        while(next != null){
            if(cur.val != next.val){
                next = next.next;
                cur = cur.next;
                prev = prev.next;
            }else {
                while (next != null && next.val == cur.val){
                    next = next.next;
                    prev.next = next;
                }
                if(next != null){
                    cur = next;
                    next = next.next;
                }
            }
        }
        return dummy.next;
    }
}
