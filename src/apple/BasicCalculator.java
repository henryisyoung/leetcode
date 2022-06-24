package apple;

import java.util.Stack;

public class BasicCalculator {
    public static int calculate(String s) {
        int result = 0;
        if (s == null || s.length() == 0) {
            return result;
        }
        Stack<Integer> stack = new Stack<>();
        int sign = 1;
        for (int i = 0; i < s.length(); i++) {
            char cur = s.charAt(i);
            if (Character.isDigit(cur)) {
                int val = 0;
                while ((i < s.length() && Character.isDigit(s.charAt(i)))) {
                    val = val * 10 + s.charAt(i++) - '0';
                }
                i--;
                result += sign * val;
            } else if (cur == '+') {
                sign = 1;
            } else if (cur == '-') {
                sign = -1;
            } else if (cur == '(') {
                stack.push(sign);
                stack.push(result);
                result = 0;
                sign = 1;
            } else if (cur == ')') {
                result = stack.pop() + stack.pop() * result;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println(calculate("(1+(4+5+2)-3)+(6+8)"));
    }
}
