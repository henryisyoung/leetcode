package leetcode;

public class Solution19 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ListNode l1 = new ListNode(1);
		//l1.next = new ListNode(1);
		//l1=l1.next;
		Solution19 t = new Solution19();
		
		System.out.println(t.removeNthFromEnd(l1, 1));
	}

	public ListNode removeNthFromEnd(ListNode head, int n) {
		ListNode front = head;
		ListNode back = head;
		ListNode pre = null;
		while(front !=null && n>0){
			front = front.next;
			n--;
		}
		if(front == null){ return head.next;}
		while(front!=null){
			pre = back;
			back = back.next;
			front = front.next;
		}
		pre.next = back.next;
		return head;
        
    }

}
