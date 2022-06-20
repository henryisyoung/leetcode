package datastructure.tree;

import java.util.Stack;

public class VerifyPreorderSequenceBST {
    public boolean verifyPreorder(int[] preorder) {
        if (preorder == null || preorder.length == 0) return true;

        Stack<Integer> stack = new Stack<>();
        int min = Integer.MIN_VALUE;

        for (int i : preorder) {
            if (min > i) {
                return false;
            }
            while (!stack.isEmpty() && stack.peek() < i) {
                min = stack.pop();
            }
            stack.push(i);
        }

        return true;
    }
}
