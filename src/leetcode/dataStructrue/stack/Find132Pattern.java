package leetcode.dataStructrue.stack;

import java.util.Stack;

public class Find132Pattern {
    public static boolean find132pattern(int[] nums) {
        if (nums == null || nums.length < 3) return false;
        Stack<Integer> stack = new Stack<>();
        for (int i : nums) {
            int count = 0;
            while (!stack.isEmpty() && i < stack.peek()) {
                stack.pop();
                count++;
            }
            if (count > 0 && !stack.isEmpty()) return true;
            stack.push(i);
        }
        return false;
    }

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4};
        int[] nums2 = {3, 1, 4, 2};
        int[] nums3 = {-1, 3, 2, 0};
        System.out.println(find132pattern(nums));
        System.out.println(find132pattern(nums2));
        System.out.println(find132pattern(nums3));
    }
}
