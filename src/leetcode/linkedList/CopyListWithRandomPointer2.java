package leetcode.linkedList;

import java.util.HashMap;
import java.util.Map;

public class CopyListWithRandomPointer2 {
    class Node {
        public int val;
        public Node next;
        public Node random;

        public Node() {}

        public Node(int _val,Node _next,Node _random) {
            val = _val;
            next = _next;
            random = _random;
        }
    }
    public Node copyRandomList(Node head) {
        if (head == null) return head;
        Map<Node, Node> map = new HashMap<>();
        Node dummy = new Node(0, null, null);
        Node copy = dummy;
        while (head != null) {
            Node cur;
            if (!map.containsKey(head)) {
                cur = new Node(head.val, null, null);
                map.put(head, cur);
            } else {
                cur = map.get(head);
            }
            copy.next = cur;
            if (head.random != null) {
                if (map.containsKey(head.random)) {
                    cur.random = map.get(head.random);
                } else {
                    Node copyRadom = new Node(head.random.val, null, null);
                    cur.random = copyRadom;
                    map.put(head.random, copyRadom);
                }
            }
            copy = copy.next;
            head = head.next;
        }

        return dummy.next;
    }
}
