package uber;

import java.util.Stack;

public class isValidBST {
    public boolean isValidBST(int[] pre) {
        Stack<Integer> stack = new Stack<>();
        int root = Integer.MIN_VALUE;
        for (int i = 0; i < pre.length; i++) {
            if (pre[i] < root) {
                return false;
            }
            while (!stack.isEmpty() && stack.peek() < pre[i]) {
                root = stack.pop();
            }
            stack.push(pre[i]);
        }
        return true;
    }
}
