package leetcode.dataStructrue.stack;

import java.util.Stack;

public class LargestRectangleInHistogram {
    public int largestRectangleArea(int[] heights) {
        if (heights == null || heights.length == 0) {
            return 0;
        }
        int max = 0;
        Stack<Integer> stack = new Stack<>();
        for(int i = 0; i <= heights.length; i++) {
            int curHeight = i == heights.length ? Integer.MIN_VALUE: heights[i];
            while (!stack.isEmpty() && curHeight <= heights[stack.peek()]) {
                int pos = stack.pop();
                int height = heights[pos];
                int width = stack.isEmpty()? i : (i - stack.peek() - 1);
                max = Math.max(max, height * width);
            }
            stack.push(i);
        }

        return max;
    }
    public int largestRectangleArea2(int[] heights) {
        if (heights == null || heights.length == 0) {
            return 0;
        }
        Stack<Integer> stack = new Stack<>();
        int n = heights.length;
        int max = 0;
        for (int i = 0; i <= n; i++) {
            int cur = i == n ? Integer.MIN_VALUE : heights[i];
            while (!stack.isEmpty() && cur <= heights[stack.peek()]) {
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                max = Math.max(max, height * width);
            }
            stack.push(i);
        }
        return max;
    }
}
