package leetcode.solution;

public class Solution25 {
	
    public ListNode reverseKGroup(ListNode head, int k) {
        if(head==null || k==1) return head;
        return reverseKGroupHelper(head,k);
    }

	private ListNode reverseKGroupHelper(ListNode head, int k) {
		if(! isValidPhase(head, k)) return head;
		int i = k;
		ListNode cur = head, tail = cur, prev = null;
		while(i > 0){
			ListNode tmp = cur.next;
			cur.next = prev;
			prev = cur;
			cur = tmp;
			i--;
		}
		tail.next = reverseKGroupHelper(cur,k);
		return prev;
	}

	private boolean isValidPhase(ListNode head, int k) {
		if(head==null) return false;
		ListNode cur = head;
		while(cur!=null){
			k--;
			cur=cur.next;
		}
		return k<=0;
	}
	
}
