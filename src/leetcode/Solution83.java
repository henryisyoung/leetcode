package leetcode;

public class Solution83 {
    public ListNode deleteDuplicates(ListNode head) {
    	if(head==null||head.next==null) return head;
    	ListNode cur=head.next;
    	ListNode prev=head;
    	while(cur!=null){
    		if(cur.val != prev.val){
    			cur = cur.next;
    			prev = prev.next;
    		}else{
    			cur = cur.next;
    			prev.next = cur;
    		}
    	}
        return head;
    }
    
    public ListNode deleteDuplicates2(ListNode head) {
    	if(head==null||head.next==null) return head;
    	ListNode cur=head.next;
    	ListNode prev=head;
    	while(cur != null){
    		while(cur != null && cur.val == prev.val){
    			cur = cur.next;
    		}
    		if(cur == null){
    			prev.next=cur; 
    			return head;
    		}
    		prev.next=cur;
    		prev=cur;
    		cur = cur.next;
    	}
    	return head;
    }
    

}
