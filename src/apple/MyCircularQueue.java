package apple;

public class MyCircularQueue {
    int[] queue;
    int size, head, tail, count;
    /** Initialize your data structure here. Set the size of the queue to be k. */
    public MyCircularQueue(int k) {
        this.size = k;
        this.queue = new int[k];
        this.head = -1;
        this.tail = 0;
        this.count = 0;
    }

    /** Insert an element into the circular queue. Return true if the operation is successful. */
    public boolean enQueue(int value) {
        if(isFull()) return false;
        head++;
        head %= size;
        queue[head] = value;
        count++;
        return true;
    }

    /** Delete an element from the circular queue. Return true if the operation is successful. */
    public boolean deQueue() {
        if (isEmpty()) return false;
        tail++;
        tail %= size;
        count--;
        return true;
    }

    /** Get the front item from the queue. */
    public int Front() {
        if (isEmpty()) return -1;
        return queue[tail];
    }

    /** Get the last item from the queue. */
    public int Rear() {
        if (isEmpty()) return -1;
        return queue[head];
    }

    /** Checks whether the circular queue is empty or not. */
    public boolean isEmpty() {
        return count == 0;
    }

    /** Checks whether the circular queue is full or not. */
    public boolean isFull() {
        return count == size;
    }
}
