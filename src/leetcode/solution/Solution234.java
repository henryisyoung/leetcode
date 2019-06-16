package leetcode.solution;

public class Solution234 {
    public boolean isPalindrome(ListNode head) {
        ListNode newhead = reverse(head);
        return isEqule(newhead,head);
    }

	private boolean isEqule(ListNode newhead, ListNode head) {
		while(newhead != null && head != null){
			if(newhead != head) return false;
			newhead = newhead.next;
			head = head.next;
		}
		return (newhead == head);
	}

	private ListNode reverse(ListNode head) {
		ListNode prev = null;
		while(head != null){
			ListNode tmp =head.next;
			head.next = prev;
			prev = head;
			head = tmp;
		}
		return prev;
	}
}
