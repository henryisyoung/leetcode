package leetcode.dataStructrue.stack;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class EvaluateReversePolishNotation2 {
    public int evalRPN(String[] tokens) {
        Set<String> set = new HashSet<>();
        set.add("+");
        set.add("-");
        set.add("*");
        set.add("/");
        int result = 0;
        Stack<Integer> stack = new Stack<>();
        for (String token : tokens) {
            if (set.contains(token)) {
                if (stack.isEmpty()) {
                    return 0;
                } else {
                    switch (token) {
                        case "+":
                            stack.push(stack.pop() + stack.pop()); break;
                        case "-":
                            int a = stack.pop(), b = stack.pop();
                            stack.push(b - a); break;
                        case "*":
                            stack.push(stack.pop() * stack.pop()); break;
                        case "/":
                            a = stack.pop();
                            b = stack.pop();
                            stack.push(b / a); break;
                    }
                }
            } else {
                stack.push(Integer.parseInt(token));
            }
        }
        return stack.peek();
    }
}
