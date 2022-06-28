package apple;

import java.util.Deque;
import java.util.LinkedList;

class JumpGameVI {
    public static int maxResult(int[] nums, int k) {
        int n = nums.length;
        int[] dp = new int[n];
        dp[0] = nums[0];
        Deque<Integer> deque = new LinkedList<>();
        deque.offerLast(0);
        for (int i = 1; i < n; i++) {
            while (!deque.isEmpty() && deque.peekFirst() < (i - k)) {
                deque.pollFirst();
            }
            dp[i] = nums[i] + dp[deque.peek()];
            while (!deque.isEmpty() && dp[deque.peekLast()] <= dp[i]) {
                deque.pollLast();
            }
            deque.offerLast(i);
        }
        return dp[n - 1];
    }

    public static void main(String[] args) {
        int[] nums = {1,-1,-2,4,-7,4};
        int k = 2;
        System.out.println(maxResult(nums, k));
    }
}