package leetcode.solution;

public class Question86_Linked_List {
    public ListNode partition(ListNode head, int x) {
        ListNode dummy = new ListNode(0);
        ListNode dummy2 = new ListNode(0);
        ListNode s = dummy;
        ListNode b = dummy2;
        while (head != null){
            int v = head.val;
            if(v < x){
                s.next = new ListNode(v);
                s = s.next;
            }else {
                b.next = new ListNode(v);
                b = b.next;
            }
            head = head.next;
        }
        b.next = null;
        s.next = dummy2.next;
        return dummy.next;
    }
}
