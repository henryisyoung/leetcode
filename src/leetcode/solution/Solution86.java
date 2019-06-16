package leetcode.solution;

public class Solution86 {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution86 t = new Solution86();
		ListNode head =  new ListNode(1);
		head.next =  new ListNode(4);
		head.next.next =  new ListNode(3);
		head.next.next.next =  new ListNode(2);
		head.next.next.next.next =  new ListNode(5);
		head.next.next.next.next.next =  new ListNode(2);
		System.out.println(t.partition(head, 3));
	}
    public ListNode partition(ListNode head, int x) {
    	ListNode tmp1 = new ListNode(0);
    	ListNode thead1 = tmp1;
    	ListNode tmp2 = new ListNode(0);
    	ListNode thead2 = tmp2;
    	while(head != null){
    		if(head.val < x){
    			thead1.next = head;
    			thead1 = thead1.next;
    		}else{
    			thead2.next = head;
    			thead2 = thead2.next;
    		}
    		head = head.next;
    	}
    	thead2.next = null;
    	thead1.next = tmp2.next;
    	return tmp1.next;
    }
}
