package leetcode.binaryTree;

import java.util.*;

public class VerifyPreorderSequenceInBinarySearchTree {
    public boolean verifyPreorder(int[] preorder) {
        int low = Integer.MIN_VALUE;
        Stack<Integer> stack = new Stack<>();
        for (int cur : preorder) {
            if (cur < low) {
                return false;
            }
            while (!stack.isEmpty() && stack.peek() < cur) {
                low = stack.pop();
            }
            stack.push(cur);
        }
        return true;
    }
    public boolean verifyPreorderFaster(int[] preorder) {
        int low = Integer.MIN_VALUE, i = -1;
        for (int cur : preorder) {
            if (cur < low) {
                return false;
            }
            while (i >= 0 && preorder[i] < cur) {
                low = preorder[i--];
            }
            preorder[++i] = cur;
        }
        return true;
    }
}
