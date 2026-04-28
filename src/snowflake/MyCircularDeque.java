package snowflake;

public class MyCircularDeque {

    static class Node {
        Node prev, next;
        int val;
        Node(int val) {
            this.val = val;
        }
    }

    Node head, tail;
    int size, capacity;
    public MyCircularDeque(int k) {
        this.size = 0;
        this.capacity = k;
    }

    public boolean insertFront(int value) {
        if (isFull()) return false;
        if (isEmpty()) {
            head = new Node(value);
            tail = head;
        } else {
            Node newNode = new Node(value);
            head.prev = newNode;
            newNode.next = head;
            head = head.prev;
        }
        size++;
        return true;
    }

    public boolean insertLast(int value) {
        if (isFull()) return false;
        if (isEmpty()) {
            tail = new Node(value);
            head = tail;
        } else {
            Node newNode = new Node(value);
            tail.next = newNode;
            newNode.prev = tail;
            tail = tail.next;
        }
        size++;
        return true;
    }

    public boolean deleteFront() {
        if (isEmpty()) return false;
        if (size == 1) {
            head = tail = null;
        } else {
            head = head.next;
        }
        size--;
        return true;
    }

    public boolean deleteLast() {
        if (isEmpty()) return false;
        if (size == 1) {
            head = tail = null;
        } else {
            tail = tail.prev;
        }
        size--;
        return true;
    }

    public int getFront() {
        if (isEmpty()) return -1;
        return head.val;
    }

    public int getRear() {
        if (isEmpty()) return -1;
        return tail.val;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }
}
