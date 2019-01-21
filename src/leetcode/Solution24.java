package leetcode;

public class Solution24 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution24 t = new Solution24();
		ListNode a = new ListNode(1);
		a.next = new ListNode(2);
		a.next.next = new ListNode(3);
		a.next.next.next = new ListNode(4);
		a.next.next.next.next = new ListNode(5);
		ListNode aa = null;
		ListNode head = t.swapPairs(aa);
		
		System.out.println(head);
	}
	
    public ListNode swapPairs(ListNode head) {
    	if(head==null) return null;
        ListNode pre = head;
        ListNode cur = head.next;
        while(cur!=null && cur.next!=null){
        	int tmp1 = pre.val;
        	int tmp2 = cur.val;
        	pre.val = tmp2;
        	cur.val = tmp1;
        	pre=pre.next.next;
        	cur=cur.next.next;
        }
        if(cur!=null){
        	int tmp1 = pre.val;
        	int tmp2 = cur.val;
        	pre.val = tmp2;
        	cur.val = tmp1;
        }
        return head;
    }
	

}
