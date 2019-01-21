package leetcode;

public class Solution92 {
    public ListNode reverseBetween(ListNode head, int m, int n) {
    	ListNode dn = new ListNode(0);
    	dn.next = head; 
    	head = dn;
    	for(int i=1; i < m; i++){
    		if(head == null) return head;
    		head = head.next;
    	}
    	ListNode prevM = head, mNode = head.next, nNode = head.next, postN = nNode.next;
    	for(int i = m; i< n; i++){
    		if(head == null) return head;
    		ListNode tmp = postN.next;
    		postN.next = nNode;
    		nNode = postN;
    		postN = tmp;
    	}
    	prevM.next = nNode; mNode.next = postN;
    	return dn.next;
    }
}
