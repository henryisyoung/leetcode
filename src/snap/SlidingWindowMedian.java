package snap;

import java.util.Collections;
import java.util.PriorityQueue;

public class SlidingWindowMedian {
    public double[] medianSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length < k) return null;
        int n = nums.length;
        double[] result = new double[n - k + 1];
        PriorityQueue<Integer> max = new PriorityQueue<>(n, Collections.reverseOrder());
        PriorityQueue<Integer> min = new PriorityQueue<>(n);
        for (int i = 0; i < n; i++) {
            max.add(nums[i]);
            min.add(max.poll());
            if (min.size() > max.size()) max.add(min.poll());
            if (max.size() - 1 > min.size()) min.add(max.poll());

            if (i + 1 - k >= 0) {
                if (k % 2 == 1) {
                    result[i - k + 1] = (double) max.peek();
                } else {
                    result[i - k + 1] = max.peek() * 0.5 + min.peek() * 0.5 ;
                }
                int removed = nums[i + 1 - k];
                if (removed > max.peek()) {
                    min.remove(removed);
                } else {
                    max.remove(removed);
                }
                if (min.size() > max.size()) max.add(min.poll());
                if (max.size() - 1 > min.size()) min.add(max.poll());
            }
        }
        return result;
    }
}
