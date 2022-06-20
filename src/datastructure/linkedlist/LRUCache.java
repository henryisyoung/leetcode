package datastructure.linkedlist;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {
    class Node{
        int key, value;
        Node prev, next;
        public Node(int key, int val) {
            this.key = key;
            this.value = val;
        }
    }
    Node head, tail;
    Map<Integer, Node> map;
    int capacity;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = this.tail = new Node(-1,-1);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        if (!map.containsKey(key)) {
            return -1;
        }
        Node node = map.get(key);
        int val = node.value;
        node.prev.next= node.next;
        node.next.prev = node.prev;
        moveTail(node);
        return val;
    }

    private void moveTail(Node node) {
        node.next = tail;
        node.prev = tail.prev;
        tail.prev.next = node;
        tail.prev = node;
    }

    public void put(int key, int value) {
        if (get(key) == -1) {
            Node node = new Node(key, value);
            map.put(key, node);
            if (map.size() > capacity) {
                Node remove = head.next;
                map.remove(remove.key);
                head.next = head.next.next;
                head.next.prev = head;
            }
            moveTail(node);
        } else {
            Node node = map.get(key);
            node.value = value;
        }
    }
}
