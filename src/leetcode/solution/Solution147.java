package leetcode.solution;

public class Solution147 {
	public ListNode insertionSortList(ListNode head) {
		if(head == null || head.next == null){
			return head;
		}
		ListNode dm = new ListNode(0);
		
		while(head != null){
			ListNode prev = dm;
			while(prev.next != null && prev.next.val <= head.val){
				prev = prev.next;
			}
			ListNode next = head.next;
			head.next = prev.next;
			prev.next = head;
			head = next;
		}
		return dm.next;
	}
}
