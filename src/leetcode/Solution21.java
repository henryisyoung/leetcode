package leetcode;

public class Solution21 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution21 t = new Solution21();
		ListNode l1 = new ListNode(1);;
		ListNode l2= new ListNode(0);
		ListNode l3 = t.mergeTwoLists( l1,  l2);
		System.out.println(t.mergeTwoLists( l1,  l2));
	}
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode cur =new ListNode(0);
        ListNode head = cur;
        ListNode st = l1;
        ListNode nd = l2;
        if(l2==null) return l1;
        if(l1==null) return l2;
        while(st!=null && nd!=null){
        		if(st.val>=nd.val){
        			head.next = nd;
            		head = head.next;
            		nd=nd.next;
        		}else {
        			head.next = st;
            		head = head.next;
            		st=st.next;
        		}
        	}
        head.next = (st==null)?nd:st;
        return cur.next;
    }
}
