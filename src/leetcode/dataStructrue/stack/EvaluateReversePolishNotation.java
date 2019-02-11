package leetcode.dataStructrue.stack;

import java.util.*;

public class EvaluateReversePolishNotation {
    public int evalRPN(String[] tokens) {
        if(tokens == null || tokens.length == 0) return 0;
        Stack<Integer> stack = new Stack<Integer>();
        Set<String> set = new HashSet<String>();
        set.add("+");
        set.add("-");
        set.add("*");
        set.add("/");

        for(String str : tokens){
            if (set.contains(str)) {
                if (stack.isEmpty()) {
                    return 0;
                } else {
                    if (str.equals("+")) {
                        stack.push(stack.pop() + stack.pop());
                    } else if (str.equals("*")) {
                        stack.push(stack.pop() * stack.pop());
                    } else if (str.equals("/")) {
                        int s1 = stack.pop(), s2 = stack.pop();
                        stack.push(s2 / s1);
                    } else {
                        int s1 = stack.pop(), s2 = stack.pop();
                        stack.push(s2 - s1);
                    }
                }
            } else {
                stack.push(Integer.parseInt(str));
            }
        }
        return stack.peek();
    }
}
