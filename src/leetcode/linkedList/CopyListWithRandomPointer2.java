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
        if (head == null) {
            return head;
        }
        Map<Node, Node> map = new HashMap<>();
        Node dummy = new Node(0, null, null);
        Node copy = dummy;
        while (head != null) {
            Node cur = null;
            if (!map.containsKey(head)) {
                cur = new Node(head.val, null, null);
                map.put(head, cur);
            } else {
                cur = map.get(head);
            }
            copy.next = cur;
            if (head.random != null) {
                Node headRand = head.random;
                if (!map.containsKey(headRand)) {
                    Node curRand = new Node(headRand.val, null, null);
                    copy.random = curRand;
                    map.put(headRand, curRand);
                } else {
                    Node curRand = map.get(headRand);
                    copy.random = curRand;
                }
            }
            head = head.next;
            copy = copy.next;
        }

        return dummy.next;
    }
}
