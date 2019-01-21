package leetcode;

public class Solution160 {
	class ResultType{
		int size;
		ListNode tail;
		public ResultType(int size, ListNode tail){
			this.size = size;
			this.tail = tail;
		}
	}
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
    	if(headA == null || headB == null) return null;
    	
    	ResultType rltA = getTailandSize(headA);
    	ResultType rltB = getTailandSize(headB);
    	
    	if(rltA.tail != rltB.tail){
    		return null;
    	}
    	
    	ListNode shorter = null, longer = null;
    	if(rltA.size > rltB.size){
    		longer = headA;
    		shorter = headB;
    	}else{
    		longer = headB;
    		shorter = headA;
    	}
    		
    	int k = Math.abs(rltA.size - rltB.size);
    	longer = getKthNode(longer,k);

    	while(longer != shorter){
    		longer = longer.next;
    		shorter = shorter.next;
    	}
    	return longer;
    }
 

	private ListNode getKthNode(ListNode head, int k) {
		while(k > 0 && head !=null) {
			k--;
			head = head.next;
		}
		return head;
	}


	private ResultType getTailandSize(ListNode head) {
		if( head == null) return  null;
		int size = 1;
		while(head.next != null){
			head = head.next;
			size++;
		}
		return new ResultType(size,head);
	}   	
    
}
