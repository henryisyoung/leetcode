package leetcode.solution;

public class Solution328 {
    public ListNode oddEvenList(ListNode head) {
        if(head == null || head.next == null){
        	return head;
        }
        ListNode p1 = head, p2 = head.next, p2Head= head.next;
        while(p1 != null && p2 != null && p2.next != null){
        	p1.next = p2.next;
        	p1 = p1.next;
        	
        	p2.next = p1.next;
        	p2 = p2.next;
        }
        p1.next = p2Head;
        return head;
    }
}
