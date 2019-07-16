package leetcode.dataStructrue;

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

    int capacity;
    Map<Integer, Node> map;
    Node head, tail;
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = new Node(Integer.MIN_VALUE, Integer.MIN_VALUE);
        this.tail = new Node(Integer.MIN_VALUE, Integer.MIN_VALUE);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        if (!map.containsKey(key)) return -1;
        Node n = map.get(key);
        n.prev.next = n.next;
        n.next.prev = n.prev;
        moveTail(n);
        return n.val;
    }

    private void moveTail(Node n) {
        n.next = tail;
        n.prev = tail.prev;
        tail.prev = n;
        n.prev.next = n;
    }

    public void put(int key, int value) {
        if (get(key) == -1) {
            if (map.size() == capacity) {
                map.remove(head.next.key);
                head.next = head.next.next;
                head.next.prev = head;
            }
            Node n = new Node(key, value);
            map.put(key, n);
            moveTail(n);
        } else {
            Node n = map.get(key);
            n.val = value;
            map.put(key, n);
        }
    }
}
