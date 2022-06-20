package datastructure.Heap;

import java.util.PriorityQueue;

public class MedianFinder {
    PriorityQueue<Integer> min, max;

    /** initialize your data structure here. */
    public MedianFinder() {
        this.max = new PriorityQueue<>((a, b) -> (b - a));
        this.min = new PriorityQueue<>();
    }

    public void addNum(int num) {
        max.add(num);
        min.add(max.poll());
        if (min.size() > max.size()) {
            max.add(min.poll());
        }
    }

    public double findMedian() {
        if (min.size() == max.size()) {
            return 0.5 * (min.peek() + max.peek());
        } else {
            return max.peek();
        }
    }
}
