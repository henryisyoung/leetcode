package leetcode.dataStructrue.heap;

import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;

public class SlidingWindowMedian2 {
    public double[] medianSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length < k) {
            return null;
        }
        int n = nums.length;
        double[] result = new double[n - k + 1];
        PriorityQueue<Integer> max = new PriorityQueue<>(n, Collections.reverseOrder());
        PriorityQueue<Integer> min = new PriorityQueue<>(n);

        for (int i = 0; i < n; i++) {
            max.add(nums[i]);
            min.add(max.poll());
            if (max.size() < min.size()) {
                max.add(min.poll());
            }
            if (max.size()  > min.size() + 1) {
                min.add(max.poll());
            }
            if (i - k + 1 >= 0) {
                if (k % 2 == 1) {
                    result[i - k + 1] = (double) max.peek();
                } else {
                    result[i - k + 1] = max.peek() * 0.5 + min.peek() * 0.5 ;
                }
                int val = nums[i - k + 1];
                if (val > max.peek()) {
                    min.remove(val);
                } else {
                    max.remove(val);
                }
                if (max.size() < min.size()) {
                    max.add(min.poll());
                }
                if (max.size() > min.size() + 1) {
                    min.add(max.poll());
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[] nums = {1,3,-1,-3,5,3,6,7};
        int k = 3;
        SlidingWindowMedian2 solver = new SlidingWindowMedian2();
        double[] result = solver.medianSlidingWindow(nums, k);
        System.out.println(Arrays.toString(result));
    }
}
