package facebook;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {
    class Node{
        Node prev, next;
        int key, val;
        public Node(int key, int val) {
            this.val = val;
            this.key = key;
        }
    }
    Map<Integer, Node> map;
    int capacity;
    Node head, tail;
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = new Node(Integer.MAX_VALUE, Integer.MAX_VALUE);
        this.tail = new Node(Integer.MAX_VALUE, Integer.MAX_VALUE);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        if (!map.containsKey(key)) return -1;
        Node n = map.get(key);
        n.next.prev = n.prev;
        n.prev.next = n.next;
        moveTail(n);
        return n.val;
    }

    private void moveTail(Node n) {
        n.prev = tail.prev;
        tail.prev = n;
        n.next = tail;
        n.prev.next = n;
    }

    public void put(int key, int value) {
        if (get(key) == -1) {
            Node n = new Node(key, value);
            map.put(key, n);
            moveTail(n);
            if (map.size() > capacity) {
                map.remove(head.next.key);
                head.next = head.next.next;
                head.next.prev = head;
            }
        } else {
            map.get(key).val = value;
        }
    }
}
