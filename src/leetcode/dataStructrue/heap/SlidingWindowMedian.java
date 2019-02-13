package leetcode.dataStructrue.heap;

import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

public class SlidingWindowMedian {
    public double[] medianSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length == 0) {
            return null;
        }
        int n = nums.length;
        int m = n - k + 1; // 结果的尺寸
        double[] result = new double[m];
        //两个堆，一个最大堆，一个最小
        PriorityQueue<Integer> maxHeap = new PriorityQueue<Integer>(k, Collections.reverseOrder());
        PriorityQueue<Integer> minHeap = new PriorityQueue<Integer>(k);
        for (int i = 0; i < n; i++){
            int val = nums[i];
            maxHeap.add(val);
            minHeap.add(maxHeap.poll());
            if (minHeap.size() > maxHeap.size()) {
                maxHeap.add(minHeap.poll());
            }
            if (maxHeap.size() > minHeap.size() + 1) {
                minHeap.add(maxHeap.poll());
            }
            if (i - k + 1 >= 0) {
                if (k % 2 == 1) {
                    result[i - k + 1] = maxHeap.peek();
                } else {
                    result[i - k + 1] = maxHeap.peek() * 0.5 + minHeap.peek() * 0.5;
                }
                int removeVal = nums[i - k + 1];
                if (removeVal <= maxHeap.peek()) {
                    maxHeap.remove(removeVal);
                } else {
                    minHeap.remove(removeVal);
                }
                if (minHeap.size() > maxHeap.size()) {
                    maxHeap.add(minHeap.poll());
                }
                if (maxHeap.size() > minHeap.size() + 1) {
                    minHeap.add(maxHeap.poll());
                }
            }
        }
        return result;
    }
}
