package leetcode.solution;

public class Solution143 {
    public void reorderList(ListNode head) {
    	if(head == null || head.next == null){
    		return;
    	}
    	
        ListNode mid= findMid(head);
        ListNode newHead = reverse(mid.next);
        mid.next = null;
        merger(head, newHead);
    }

	private void merger(ListNode l1, ListNode l2) {
		ListNode dm = new ListNode(0);
		int count = 0;
		while(l1 != null && l2 != null){
			if(count % 2 == 0){
				dm.next = l1;
				l1 = l1.next;
			}else{
				dm.next = l2;
				l2 = l2.next;
			}
			count++;
			dm = dm.next;
		}
		if(l1 != null){
			dm.next = l1;
		}else{
			dm.next = l2;
		}
	}

	private ListNode reverse(ListNode cur) {
		ListNode prev = null;
		while(cur != null){
			ListNode tmp = cur.next;
			cur.next = prev;
			prev = cur;
			cur = tmp;
		}
		return prev;
	}

	private ListNode findMid(ListNode head) {
		ListNode slow = head, fast = head.next;
		while(fast != null && fast.next != null){
			fast = fast.next.next;
			slow = slow.next;
		}
		return slow;
	}
}
