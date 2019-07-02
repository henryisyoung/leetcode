package leetcode.dataStructrue;

import java.util.*;

public class LFUCache {
    class Node {
        int key, val, count;
        Node prev, next;
        public Node(int key, int val) {
            this.key = key;
            this.count = 1;
            this.val = val;
        }
    }

    class DoubleLinkedList{
        Node front, tail;
        int size;

        public DoubleLinkedList() {
            this.front = new Node(-1, -1);
            this.tail = new Node(-1, -1);
            front.next = tail;
            tail.prev = front;
            size = 0;
        }

        public void insertToFront(Node n) {
            n.next = front.next;
            front.next = n;
            n.next.prev = n;
            n.prev = front;
            size++;
        }

        public void remove(Node n) {
            Node prev = n.prev;
            Node next = n.next;
            prev.next = next;
            next.prev = prev;
            size--;
        }
    }

    private Map<Integer, Node> keyMap;
    private Map<Integer, DoubleLinkedList> countMap;
    int leastCount, cap;

    public LFUCache(int cap) {
        this.cap = cap;
        this.countMap = new HashMap<>();
        this.leastCount = Integer.MAX_VALUE;
        this.keyMap = new HashMap<>();
    }

    public int get(int key) {
        if (!keyMap.containsKey(key)) {
            return -1;
        }
        Node n = keyMap.get(key);
        promote(n);
        return n.val;
    }

    public void put(int key, int value) {
        if (keyMap.containsKey(key)) {
            Node n = keyMap.get(key);
            n.val = value;
            promote(n);
            return;
        }
        if (cap == 0) return;
        if (keyMap.size() == cap) {
            removeLeastCountNode();
        }
        Node n = keyMap.put(key, new Node(key, value));
        countMap.putIfAbsent(n.count, new DoubleLinkedList());
        countMap.get(n.count).insertToFront(n);
        leastCount = 1;
    }

    private void removeLeastCountNode() {
        DoubleLinkedList list = countMap.get(leastCount);
        Node node = list.tail.prev;
        list.remove(node);
        keyMap.remove(node.key);
    }

    private void promote(Node n) {
        DoubleLinkedList list = countMap.get(n.count);
        list.remove(n);
        if (leastCount == n.count && list.size == 0) leastCount++;
        n.count++;
        countMap.putIfAbsent(n.count, new DoubleLinkedList());
        countMap.get(n.count).insertToFront(n);
    }
}
