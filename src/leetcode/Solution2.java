package leetcode;

public class Solution2 {
	
	public static void main(String[] args) {
		ListNode l1 = new ListNode(0);
		l1.next = new ListNode(1);
		l1=l1.next;
		System.out.println(l1.val);
	}
	
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode digit1 =  l1;
        ListNode digit2 =  l2;
        ListNode cur = new ListNode(0);
        ListNode head = cur;
        int value = 0;
        while(digit1!=null || digit2!=null){
        	if(digit1!=null){
        		value += digit1.val;
        		digit1 = digit1.next;
        	}
        	if(digit2!=null){
        		value += digit2.val;
        		digit2 = digit2.next;
        	}
        	head.next = new ListNode(value%10);
        	value /= 10;
        	head = head.next;
        }
        if(value==1) head.next = new ListNode(1);
        return cur.next;
    }
    
}
