package leetcode.solution;

public class Solution148 {
	// Merge Sort
    public ListNode sortList(ListNode head) {
        if(head == null || head.next == null) return head;
        ListNode mid = findMid(head);
        ListNode right = sortList(mid.next);
        mid.next = null;
        ListNode left = sortList(head);
	    return merge2List(left,right);
    }

	private ListNode merge2List(ListNode left, ListNode right) {
		ListNode dm = new ListNode(0);
		ListNode cur = dm.next;
		while(left != null && right != null){
			if(left.val > right.val){
				cur = right;
				cur = cur.next;
				right = right.next;
			}else{
				cur = left;
				cur = cur.next;
				right = left.next;
			}
		}
		if(left != null) cur = left;
		if(right != null) cur = right;
		return dm.next;
	}

	private ListNode findMid(ListNode head) {
		ListNode slow = head, fast = head.next;
		while(fast != null && fast.next != null){
			fast = fast.next.next;
			slow = slow.next;
		}
		return slow;
	}
	
	// Quick Sort
	public ListNode sortList2(ListNode head) {
		if(head == null || head.next == null) return head;
		
		ListNode leftDM = new ListNode(0), leftCur = leftDM,
				rightDM = new ListNode(0), rightCur = rightDM,
				midDM = new ListNode(0), midCur = midDM;
		
		ListNode midPivot = findMid(head);
		
		while(head != null){
			if(head.val > midPivot.val){
				rightCur.next = head;
				rightCur = rightCur.next;
			}else if(head.val < midPivot.val){
				leftCur.next = head;
				leftCur = leftCur.next;
			}else{
				midCur.next = head;
				midCur = midCur.next;
			}
			head = head.next;
		}
		rightCur.next = null;
		leftCur.next = null;
		midCur.next = null;
		ListNode left = sortList2(leftDM.next);
		ListNode right = sortList2(rightDM.next);
		return concat(left,midDM.next,right);
	}

	private ListNode concat(ListNode left, ListNode mid, ListNode right) {
        ListNode dummy = new ListNode(0), tail = dummy;
        
        tail.next = left; tail = getTail(tail);
        tail.next = mid; tail = getTail(tail);
        tail.next = right;
        return dummy.next;
	}

	private ListNode getTail(ListNode head) {
		if(head == null || head.next == null) return head;
		while(head.next != null) head = head.next;
		return head;
	}
}
