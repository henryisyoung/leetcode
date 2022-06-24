package apple;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {
    class Node {
        int key, val;
        Node next, prev;
        public Node(int key, int val) {
            this.key = key;
            this.val = val;
        }
    }

    Node head, tail;
    Map<Integer, Node> map;
    int capacity;
    public LRUCache(int capacity) {
        this.head = new Node(-1,-1);
        this.tail = new Node(-1,-1);
        head.next = tail;
        tail.prev = head;
        this.capacity = capacity;
        this.map = new HashMap<>();
    }

    public int get(int key) {
        if (!map.containsKey(key)) return -1;

        Node node = map.get(key);
        node.prev.next = node.next;
        node.next.prev = node.prev;
        moveTail(node);
        return node.val;
    }

    private void moveTail(Node node) {
        node.prev = tail.prev;
        node.next = tail;
        tail.prev.next = node;
        tail.prev = node;
    }

    public void put(int key, int value) {
        if (get(key) != -1) {
            map.get(key).val = value;
        } else {
            Node node = new Node(key, value);
            map.put(key, node);
            moveTail(node);
            if (map.size() > capacity) {
                map.remove(head.next.key);
                head.next = head.next.next;
                head.next.prev = head;
            }
        }
    }
}
