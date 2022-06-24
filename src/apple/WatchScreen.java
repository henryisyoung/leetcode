package apple;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class WatchScreen {
    public static List<Integer> watchScreen(int[] heights) {
        List<Integer> result = new ArrayList<>();
        if (heights == null || heights.length == 0) {
            return result;
        }

        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < heights.length; i++) {
            if (stack.isEmpty()) {
                stack.push(i);
            } else if(heights[i] >= heights[stack.peek()]) {
                stack.push(i);
            }
        }

        result.addAll(stack);
        return result;
    }
    public static List<Integer> watchScreen2(int[] heights, int d) {
        List<Integer> result = new ArrayList<>();
        if (heights == null || heights.length == 0) {
            return result;
        }

        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < heights.length; i++) {
            if (stack.isEmpty()) {
                stack.push(i);
            } else if(heights[i] >= heights[stack.peek()]) {
                stack.push(i);
            } else if (heights[i] < heights[stack.peek()] && (i - stack.peek()) >= d) {
                stack.push(i);
            }
        }

        result.addAll(stack);
        return result;
    }

    public static void main(String[] args) {
        int[] height = {5,3,4,2,9,1};
        System.out.println(watchScreen(height));
        System.out.println(watchScreen2(height, 2));
    }
}
