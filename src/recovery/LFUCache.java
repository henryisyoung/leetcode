package recovery;

import java.util.HashMap;
import java.util.Map;

public class LFUCache {

    class Node {
        int count, val, key;
        Node next, prev;
        public Node(int count, int val, int key) {
            this.count = count;
            this.val = val;
            this.key = key;
        }
    }

    class DoublyLinkedList {
        Node head, tail;
        int size;
        public DoublyLinkedList() {
            this.head = new Node(-1, -1, -1);
            this.tail = new Node(-1, -1, -1);
            this.size = 0;
            head.next = tail;
            tail.prev = head;
        }

        public void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
        }

        public void addFront(Node node) {
            head.next.prev = node;
            node.next = head.next;
            head.next = node;
            node.prev = head;
            size++;
        }
    }

    Map<Integer, Node> map;
    Map<Integer, DoublyLinkedList> countMap;
    int capacity, leastCount;
    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.countMap = new HashMap<>();
        this.leastCount = 0;
    }

    public int get(int key) {
        if (!map.containsKey(key)) {
            return -1;
        }

        Node node = map.get(key);
        promote(node);

        return node.val;
    }

    private void promote(Node node) {
        // remove node from old list (after removal, if list is empty, delete list, least count ++)
        // insert node to a new list front
        DoublyLinkedList oldList = countMap.get(node.count);
        oldList.remove(node);

        if (oldList.size == 0 && leastCount == node.count) leastCount++;

        node.count++;
        countMap.putIfAbsent(node.count, new DoublyLinkedList());
        countMap.get(node.count).addFront(node);
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.val = value;
            promote(node);
            return;
        }

        if (capacity == 0) return;

        if (capacity == map.size()) {
            removeLeastCount();
        }

        Node node = new Node(1, value, key);
        map.put(key, node);
        countMap.putIfAbsent(node.count, new DoublyLinkedList());
        countMap.get(node.count).addFront(node);
        leastCount = 1;
    }

    private void removeLeastCount() {
        DoublyLinkedList list = countMap.get(leastCount);
        Node remove = list.tail.prev;
        list.remove(remove);
        map.remove(remove.key);
    }
}
