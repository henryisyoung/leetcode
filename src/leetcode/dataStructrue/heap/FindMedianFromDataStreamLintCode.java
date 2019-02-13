package leetcode.dataStructrue.heap;

import java.util.Collections;
import java.util.PriorityQueue;

public class FindMedianFromDataStreamLintCode {
    public int[] medianII(int[] nums) {
        if (nums == null || nums.length == 0) {
            return nums;
        }
        int n = nums.length;
        int[] medians = new int[n];
        PriorityQueue<Integer> minPQ = new PriorityQueue<>(n);
        PriorityQueue<Integer> maxPQ = new PriorityQueue<Integer>(n, Collections.reverseOrder());
        for (int i = 0; i < n; i++){
            maxPQ.add(nums[i]);
            minPQ.add(maxPQ.poll());
            if(maxPQ.size() < minPQ.size()) {
                maxPQ.add(minPQ.poll());
            }
            medians[i] = maxPQ.peek();
        }

        return medians;
    }
    public int[] medianII2(int[] nums) {
        if (nums == null || nums.length == 0) {
            return null;
        }
        int n = nums.length;
        int[] result = new int[n];
        PriorityQueue<Integer> maxPQ = new PriorityQueue<>(n, Collections.reverseOrder());
        PriorityQueue<Integer> minPQ = new PriorityQueue<>();
        int count = 0;
        for (int i : nums) {
            maxPQ.add(i);
            minPQ.add(maxPQ.poll());
            if (minPQ.size() > maxPQ.size()) {
                maxPQ.add(minPQ.poll());
            }
            result[count++] = maxPQ.peek();
        }
        return result;
    }
}
