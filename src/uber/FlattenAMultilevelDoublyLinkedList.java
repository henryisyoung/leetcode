package uber;

public class FlattenAMultilevelDoublyLinkedList {
    class Node {
        public int val;
        public Node prev;
        public Node next;
        public Node child;

        public Node() {}

        public Node(int _val,Node _prev,Node _next,Node _child) {
            val = _val;
            prev = _prev;
            next = _next;
            child = _child;
        }
    }
    public Node flatten(Node head) {
        if (head == null) return head;
        if (head.child != null) {
            Node headNext = flatten(head.next);
            Node headChild = flatten(head.child);
            head.next = headChild;
            headChild.prev = head;
            head.child = null;
            Node cur = head;
            while (cur.next != null) {
                cur = cur.next;
            }
            cur.next = headNext;
            if (headNext != null) {
                headNext.prev = cur;
            }
        } else {
            Node headNext = flatten(head.next);
            head.next = headNext;
            if (headNext != null) headNext.prev = head;
        }
        return head;
    }
}
