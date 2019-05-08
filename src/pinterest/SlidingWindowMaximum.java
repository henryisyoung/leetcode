package pinterest;

import java.util.*;

public class SlidingWindowMaximum {
    class Node {
        int pos, val;
        public Node(int pos, int val) {
            this.pos = pos;
            this.val = val;
        }
    }

    public int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length < k) {
            return null;
        }
        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Node> deque = new ArrayDeque<>();
        for (int i = 0; i < k; i++) {
            while (!deque.isEmpty() && deque.peekFirst().val <= nums[i]) {
                deque.pollFirst();
            }
            deque.addFirst(new Node(i, nums[i]));
        }
        result[0] = deque.peekLast().val;
        for (int i = k; i < n; i++) {
            while (!deque.isEmpty() && deque.peekFirst().val <= nums[i]) {
                deque.pollFirst();
            }
            deque.addFirst(new Node(i, nums[i]));
            int leftRemove = i - k;
            while (!deque.isEmpty() && deque.peekLast().pos <= leftRemove) {
                deque.pollLast();
            }
//            System.out.println(i);
            result[i - k + 1] = deque.peekLast().val;;
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
