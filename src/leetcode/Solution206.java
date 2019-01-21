package leetcode;

public class Solution206 {
    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        while(head != null){
        	ListNode tmp = head.next;
        	head.next = prev;
        	prev = head;
        	head = tmp;
        }
        return prev;
    }
}
