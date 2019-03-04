package airbnb;

import java.util.*;

public class QueueWithLimitedSizefOfArrays {

    private int fixedSize;
    private int count;
    private int head;
    private int tail;
    private List<Object> headList; private List<Object> tailList;

    public QueueWithLimitedSizefOfArrays(int fixedSize) {
        this.fixedSize = fixedSize;
        this.count = 0;
        this.head = 0;
        this.tail = 0;
        this.headList = new ArrayList<>();
        this.tailList = this.headList;
    }

    public void enqueue(int num) {
        if (tail == fixedSize - 1) {
            List<Object> newList = new ArrayList<>();
            newList.add(num);
            tailList.add(newList);
            tailList = newList;
            tail = 0;
        } else {
            tailList.add(num);
        }
        count++;
        tail++;
    }

    public Integer pop() {
        if (count == 0) {
            return null;
        }
        int num = (int)headList.get(head);
        head++;
        count--;
        if (head == fixedSize - 1) {
            List<Object> newList = (List<Object>)headList.get(head);
            headList.clear();
            headList = newList;
            head = 0;
        }
        return num;
    }

    public Integer peek() {
        if (count == 0) {
            return null;
        }
        int num = (int)headList.get(head);
        return num;
    }

    public int size() {
        return count;
    }

    public static void main(String[] args) {
        QueueWithLimitedSizefOfArrays m = new QueueWithLimitedSizefOfArrays(5);
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
