package leetcode.graphAndSearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

public class FindMedianFromDataStream {
    /** initialize your data structure here. */
    PriorityQueue<Integer> minPQ, maxPQ;

    public FindMedianFromDataStream() {
        this.maxPQ = new PriorityQueue<>(Collections.reverseOrder());
        this.minPQ = new PriorityQueue<>();
    }

    public void addNum(int num) {
        maxPQ.add(num);
        minPQ.add(maxPQ.poll());
        if(minPQ.size() > maxPQ.size()) {
            maxPQ.add(minPQ.poll());
        }
    }

    public double findMedian() {
        if(maxPQ.size() == minPQ.size()) {
            return 0.5 * (minPQ.peek() + maxPQ.peek());
        } else {
            return maxPQ.peek();
        }
    }
}
