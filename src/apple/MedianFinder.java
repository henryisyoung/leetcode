package apple;

import java.util.PriorityQueue;

public class MedianFinder {
    PriorityQueue<Integer> min;
    PriorityQueue<Integer> max ;
    public MedianFinder() {
        this.min =  new PriorityQueue<>();
        this.max = new PriorityQueue<>((a, b) -> (b - a));
    }

    public void addNum(int num) {
        min.add(num);
        max.add(min.poll());
        if (max.size() > min.size()) {
            min.add(max.poll());
        }
    }

    public double findMedian() {
        if (min.size() > max.size()) {
            return min.peek();
        } else {
            return 0.5 * (min.peek() + max.peek());
        }
    }
}
