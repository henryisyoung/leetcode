package airbnb;

import java.util.ArrayList;
import java.util.List;

public class QueueWithLimitedSizefOfArrays2 {
    int head, tail, count, fixedSize;
    Object[] headList, tailList;

    public QueueWithLimitedSizefOfArrays2(int fixedSize) {
        this.count = 0;
        this.fixedSize = fixedSize;
        this.head = 0;
        this.tail = 0;
        this.headList = new Object[fixedSize];
        this.tailList = headList;
    }

    public void enqueue(int num) {
        if (tail == fixedSize - 1) {
            Object[] next = new Object[fixedSize];
            next[0] = num;
            tailList[tail] = next;
            tailList = next;
            tail = 0;
        } else {
            tailList[tail] = num;
        }
        count++;
        tail++;
    }

    public Integer pop() {
        if (count == 0) {
            return null;
        }
        int val = (int) headList[head];
        head++;
        if (head == fixedSize - 1) {
            headList = (Object[]) headList[head];
            head = 0;
        }
        count--;
        return val;
    }

    public Integer peek() {
        if (count == 0) {
            return null;
        }
        return (int) (int) headList[head];
    }

    public int size() {
        return count;
    }

    public static void main(String[] args) {
        QueueWithLimitedSizefOfArrays2 m = new QueueWithLimitedSizefOfArrays2(5);
        m.enqueue(1);
        m.enqueue(2);
        m.enqueue(3);
        System.out.println(m.peek());
        System.out.println(m.pop());
        System.out.println(m.peek());
        System.out.println(m.pop());
        m.enqueue(4);
        m.enqueue(5);
        m.enqueue(6);
        m.enqueue(7);
        m.enqueue(8);
        m.enqueue(9);
        System.out.println(m.peek());
        System.out.println(m.pop());
        System.out.println(m.peek());
        System.out.println(m.pop());
        System.out.println(m.peek());
        System.out.println(m.pop());
        System.out.println(m.peek());
        System.out.println(m.pop());
        System.out.println(m.peek());
        System.out.println(m.pop());
        System.out.println(m.peek());
        System.out.println(m.pop());
        System.out.println(m.peek());
        System.out.println(m.pop());
        System.out.println(m.peek());
        System.out.println(m.pop());
    }
}
