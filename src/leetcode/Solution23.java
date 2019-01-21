package leetcode;

public class Solution23 {
	
	public ListNode mergeKLists(ListNode[] lists) {
		int len = lists.length;
		if(len==0) return null;
		return mergeKListsHelper(lists,0,len-1);
    }
	
    private ListNode mergeKListsHelper(ListNode[] lists, int left, int right) {
		if(left==right) return lists[left];
		if(left+1==right) return mergeTwoLists(lists[left],lists[right]);
		else{
			int mid = (left+right)/2;
			return mergeTwoLists(mergeKListsHelper(lists,left,mid),mergeKListsHelper(lists,mid+1,right));
		}
		
	}

	private ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode cur =new ListNode(0);
        ListNode head = cur;
        ListNode st = l1;
        ListNode nd = l2;
        if(l1!=null && l2==null) return l1;
        if(l2!=null && l1==null) return l2;
        while(st!=null && nd!=null){
        		if(st.val>=nd.val){
        			head.next = new ListNode(nd.val);
            		head = head.next;
            		nd=nd.next;
        		}else {
        			head.next = new ListNode(st.val);
            		head = head.next;
            		st=st.next;
        		}
        	}
        head.next = (st==null)?nd:st;
        return cur.next;
    }

}
