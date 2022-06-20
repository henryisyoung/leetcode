package datastructure.stack;

import java.util.Stack;

public class LargestRectangleInHistogram {
    public int largestRectangleArea(int[] heights) {
        int max = 0;
        Stack<Integer> stack = new Stack<>();
        int n = heights.length;
        for (int i = 0; i <= n; i++) {
            int curH = i == n ? -1 : heights[i];
            while (!stack.isEmpty() && curH < heights[stack.peek()]) {
                int pos = stack.pop();
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                int h = heights[pos];
                max = Math.max(max, h * width);
            }
            stack.push(i);
        }

        return max;
    }
}
