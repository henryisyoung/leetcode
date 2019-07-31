package leetcode.dataStructrue.heap;

import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

public class FindMedianFromDataStream {
    /** initialize your data structure here. */
    PriorityQueue<Integer> max, min;

    public FindMedianFromDataStream() {
        this.max = new PriorityQueue<>();
        this.min = new PriorityQueue<>(Collections.reverseOrder());
    }

    public void addNum(int num) {
        max.add(num);
        min.add(max.poll());
        if (max.size() < min.size()) max.add(min.poll());
    }

    public double findMedian() {
        if (max.size() > min.size()) return max.peek();
        return 0.5 * (max.peek() + min.peek());
    }
}
