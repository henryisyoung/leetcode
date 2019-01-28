package leetcode.graphAndSearch;

import java.util.Collections;
import java.util.PriorityQueue;

public class FindMedianFromDataStreamLintCode {
    public int[] medianII(int[] nums) {
        if (nums == null || nums.length == 0) {
            return nums;
        }
        int n = nums.length;
        int[] medians = new int[n];
        PriorityQueue<Integer> minPQ = new PriorityQueue<>();
        PriorityQueue<Integer> maxPQ = new PriorityQueue<>(Collections.reverseOrder());
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
}
