package LinkedIn.phone.Todo;

import java.util.*;
// 给你一个数组，求子数组的个数. ·子数组需要满足first element = last element = 这个subarray中的最大的数字。
public class CountSubarrays {

        public long countSubarrays(int[] nums) {
            long ans = 0;
            Deque<int[]> stack = new ArrayDeque<>();
            // 每个元素: [value, count]
            // count 表示这个 value 在当前有效段里出现了多少次

            for (int x : nums) {
                while (!stack.isEmpty() && stack.peek()[0] < x) {
                    stack.pop();
                }

                if (!stack.isEmpty() && stack.peek()[0] == x) {
                    int[] top = stack.peek();
                    ans += top[1] + 1L;   // 单点 + 和前面每个同值位置配对
                    top[1]++;
                } else {
                    ans += 1;             // 单点子数组
                    stack.push(new int[]{x, 1});
                }
            }

            return ans;
        }

}
