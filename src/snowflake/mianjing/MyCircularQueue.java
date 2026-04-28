package snowflake.mianjing;

/*
 * Convention used here:
 *   head = index of the oldest (front) element.
 *   tail = index of the most recently written (rear) element; -1 when empty.
 *   size = number of elements currently in the queue.
 *
 * Both pointers are advanced with `(p + 1) % k` so they never overflow.
 */
public class MyCircularQueue {
    private final int[] queue;
    private final int k;
    private int head, tail, size;

    public MyCircularQueue(int k) {
        this.k = k;
        this.queue = new int[k];
        this.head = 0;
        this.tail = -1;
        this.size = 0;
    }

    public boolean enQueue(int value) {
        if (isFull()) return false;
        tail = (tail + 1) % k;
        queue[tail] = value;
        size++;
        return true;
    }

    public boolean deQueue() {
        if (isEmpty()) return false;
        head = (head + 1) % k;
        size--;
        return true;
    }

    public int Front() {
        return isEmpty() ? -1 : queue[head];
    }

    public int Rear() {
        return isEmpty() ? -1 : queue[tail];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == k;
    }

    public static void main(String[] args) {
        MyCircularQueue q = new MyCircularQueue(3);
        System.out.println(q.enQueue(1)); // true
        System.out.println(q.enQueue(2)); // true
        System.out.println(q.enQueue(3)); // true
        System.out.println(q.enQueue(4)); // false (full)
        System.out.println(q.Rear());     // 3
        System.out.println(q.isFull());   // true
        System.out.println(q.deQueue());  // true
        System.out.println(q.enQueue(4)); // true
        System.out.println(q.Rear());     // 4
        System.out.println(q.Front());    // 2
    }
}
