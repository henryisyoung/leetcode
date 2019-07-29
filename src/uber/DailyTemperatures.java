package uber;

import java.util.Arrays;
import java.util.Stack;

public class DailyTemperatures {
    class Node{
        int index;
        int temp;
        public Node(int index, int temp) {
            this.index = index;
            this.temp = temp;
        }
    }
    public int[] dailyTemperatures(int[] T) {
        if (T == null || T.length == 0) return T;
        int n = T.length;
        int[] result = new int[n];
        Stack<Node> stack = new Stack<>();
        for (int i = n - 1; i >= 0; i--) {
            int temp = T[i];
            while (!stack.isEmpty() && temp >= stack.peek().temp) {
                stack.pop();
            }
            if (stack.isEmpty()) {
                result[i] = 0;
            } else {
                result[i] = stack.peek().index - i;
            }
            stack.push(new Node(i, temp));
        }

        return result;
    }

    public static void main(String[] args) {
        DailyTemperatures solution = new DailyTemperatures();
        int[] result = solution.dailyTemperatures(new int[]{73, 74, 75, 71, 69, 72, 76, 73});
        System.out.println(Arrays.toString(result));
    }
}
