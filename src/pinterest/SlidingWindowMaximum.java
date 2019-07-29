package pinterest;

import java.util.*;

public class SlidingWindowMaximum {
    class Node {
        int value, index;
        public Node(int value, int index) {
            this.index = index;
            this.value = value;
        }
    }

    public int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k == 0 || nums.length < k) {
            return new int[0];
        }
        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Node> deque = new ArrayDeque<>();
        for (int i = 0; i < k; i++) {
            while (!deque.isEmpty() && nums[i] > deque.peekLast().value) {
                deque.pollLast();
            }
            deque.addLast(new Node(nums[i], i));
        }

        result[0] = deque.peekFirst().value;
        for (int i = k; i < n; i++) {
            int leftRemoveIndex = i - k;
            while (!deque.isEmpty() && leftRemoveIndex >= deque.peekFirst().index) {
                deque.pollFirst();
            }
            while (!deque.isEmpty() && nums[i] > deque.peekLast().value) {
                deque.pollLast();
            }
            deque.addLast(new Node(nums[i], i));
            result[leftRemoveIndex + 1] = deque.peekFirst().value;
        }
        return result;
    }

    public static void main(String[] args) {
        SlidingWindowMaximum sovler = new SlidingWindowMaximum();
        int[] nums = {1,3,-1,-3,5,3,6,7};
        int[] result = sovler.maxSlidingWindow(nums, 3);
        System.out.println(Arrays.toString(result));
    }
}
