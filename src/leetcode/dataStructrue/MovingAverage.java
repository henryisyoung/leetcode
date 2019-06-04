package leetcode.dataStructrue;

import java.util.LinkedList;
import java.util.Queue;

public class MovingAverage {
    /** Initialize your data structure here. */
    int size, sum;
    Queue<Integer> queue;
    public MovingAverage(int size) {
        this.size = size;
        int sum = 0;
        this.queue = new LinkedList<>();
    }

    public double next(int val) {
        queue.add(val);
        sum += val;
        if (queue.size() <= size) {
            return 1.0 * sum / queue.size();
        } else {
            sum -= queue.poll();
            return sum * 1.0 / size;
        }
    }
}
