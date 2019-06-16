package leetcode.solution;

public class Solution369 {
    public ListNode plusOne(ListNode head) {
        if(head == null){
        	return null;
        }
        ListNode l = reverse(head);
        ListNode dm = new ListNode(0);
        dm.next = l;
        ListNode cur = dm;
        int v = 1;
        while(cur.next != null){
        	int tmp = cur.val;
        	cur.next.val = (v + cur.next.val)%10;
        	v = (v + tmp)/10;
        	cur = cur.next;
        }
        if(v == 1){
        	cur.next = new ListNode(1);
        }
        return dm.next;
    }

	private ListNode reverse(ListNode head) {
		ListNode prev = null;
		while(head != null){
			ListNode next = head.next;
			head.next = prev;
			prev = head;
			head = next;
		}
		return prev;
	}
}
