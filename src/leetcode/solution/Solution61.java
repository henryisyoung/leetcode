package leetcode.solution;

public class Solution61 {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution61 t = new Solution61();
		ListNode head = new ListNode(1);
		head.next=new ListNode(2);
		head.next.next=new ListNode(3);
		head.next.next.next=new ListNode(4);
		head.next.next.next.next=new ListNode(5);
		System.out.println(t.rotateRight(head, 2).next.next.val);
	}
	
    public ListNode rotateRight(ListNode head, int k) {
        if(k==0||head==null) return head;
        ListNode prev=head,cur=head;
        int len=1;
        while(cur.next!=null){
        	cur=cur.next;
        	len++;
        }
        if(k%len==0) return head;
        k=k%len;
        while(len-k>0){
        	int val=prev.val;
        	cur.next=new ListNode(val);
        	cur=cur.next;
        	prev=prev.next;
        	len--;
        }
        return prev;
    }
}
