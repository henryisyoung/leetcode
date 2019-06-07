package leetcode.binaryTree;

import java.util.Stack;

public class VerifyPreorderSerializationOfBinaryTree {
    public boolean isValidSerialization(String preorder) {
        if (preorder == null || preorder.length() < 2) {
            return true;
        }
        Stack<String> stack = new Stack<>();
        String[] nodes = preorder.split(",");
        for (String n : nodes) {
            while (!stack.isEmpty() && n.equals("#") && stack.peek().equals("#")) {
                stack.pop();
                if (stack.isEmpty()) {
                    return false;
                }
                stack.pop();
            }
            stack.push(n);
        }
        return stack.pop().equals("#") && stack.isEmpty();
    }
}
