package leetcode.linkedList;

public class RemoveNthNodeFromEndOfList {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        if(n==0) return head;
        ListNode front = head;
        ListNode back = head;
        ListNode pre = null;
        while(n>0){
            front = front.next;
            n--;
        }
        if(front == null){ return head.next;}
        while(front!=null){
            pre = back;
            back = back.next;
            front = front.next;
        }
        pre.next = back.next;
        return head;
    }
}
