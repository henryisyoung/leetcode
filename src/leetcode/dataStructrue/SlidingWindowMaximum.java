package leetcode.dataStructrue;

import java.util.ArrayDeque;
import java.util.Deque;

public class SlidingWindowMaximum {
    private class Node {
        int pos, val;
        public Node (int pos, int val) {
            this.pos = pos;
            this.val = val;
        }
    }
    public int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length == 0 || nums.length < k) {
            return new int[0];
        }
        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Node> deque = new ArrayDeque<>();
        for (int i = 0; i < k; i++) {
            int val = nums[i];
            while (!deque.isEmpty() && val > deque.peekFirst().val) {
                deque.pollFirst();
            }
            deque.addFirst(new Node(i, val));
        }
        result[0] = deque.peekLast().val;
        for (int i = k; i < n; i++) {
            int val = nums[i];
            int left = i - k;
            while (!deque.isEmpty() && val > deque.peekFirst().val) {
                deque.pollFirst();
            }
            while (!deque.isEmpty() && left >= deque.peekLast().pos) {
                deque.pollLast();
            }
            deque.addFirst(new Node(i, val));
            result[i - k + 1] = deque.peekLast().val;
        }
        return result;
    }
}
