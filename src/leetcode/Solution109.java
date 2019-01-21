package leetcode;

public class Solution109 {
    public TreeNode sortedListToBST(ListNode head) {
    	if(head == null) return null;
    	return sortedListToBSTHelper(head, null);
    }

	private TreeNode sortedListToBSTHelper(ListNode start, ListNode end) {
		if(start == end){
			return null;
		}
		ListNode slow = start, fast = start;
		while(fast != end && fast.next != end){
			fast = fast.next.next;
			slow = slow.next;
		}
		TreeNode left = sortedListToBSTHelper(start,slow);
		TreeNode root = new TreeNode(slow.val);
		TreeNode right = sortedListToBSTHelper(slow.next,end);
		root.left = left;
		root.right = right;
		return root;
	}
	ListNode cur;
	public TreeNode sortedListToBST2(ListNode head) {
		if(head == null) return null;
		int size = 0;
		ListNode current = head;
		while(current != null){
			current = current.next;
			size++;
		}
		cur = head;
		return helper(size);
	}

	private TreeNode helper(int size) {
		if(size == 0) return null;
		TreeNode left = helper(size/2);
		TreeNode root = new TreeNode(cur.val);
		cur = cur.next;
		TreeNode right = helper( size - size/2 - 1);
		root.left = left;
		root.right = right;
		return root;
	}
}
