package contest;

import java.util.Stack;

public class MakeTheStringGreat {
    public static String makeGood(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (stack.isEmpty()) {
                stack.push(c);
            } else {
                if (c != stack.peek() && Character.toLowerCase(c) == Character.toLowerCase(stack.peek())) {
                    stack.pop();
                } else {
                    stack.push(c);
                }
            }
        }
        if (stack.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) sb.append(stack.pop());
        return sb.reverse().toString();
    }

    public static void main(String[] args) {
        String s = "leEeetcode";
        System.out.println(makeGood(s));
    }
}
