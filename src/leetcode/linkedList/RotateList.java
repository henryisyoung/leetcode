package leetcode.linkedList;

public class RotateList {
    public ListNode rotateRight(ListNode head, int k) {
        if (head == null || head.next == null) {
            return head;
        }
        int len=1;
        ListNode cur = head;
        while(cur.next!=null){
            cur=cur.next;
            len++;
        }
        if (k % len == 0) {
            return head;
        }
        k %= len;
        ListNode node = head;
        while (len - k > 0) {
            len--;
            cur.next = new ListNode(node.val);
            node = node.next;
            cur = cur.next;
        }
        return node;
    }
}
