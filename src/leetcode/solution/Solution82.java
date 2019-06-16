package leetcode.solution;

public class Solution82 {
    public ListNode deleteDuplicates(ListNode head) {
        if(head==null||head.next==null) return head;
        ListNode newH = new ListNode(0);
        newH.next=head;
        ListNode prev=newH,cur=head,next=head.next;
        while(next!=null){
        	if(cur.val!=next.val){
        		prev=prev.next;
        		cur=cur.next;
        		next=next.next;
        	}else{
        		while(next!=null&&cur.val==next.val){
        			next=next.next;
        			prev.next=next;
        		}if(next!=null){
        			cur=next;
        			next=next.next;
        		}
        	}
        }
        return newH.next;
    }
    

}
