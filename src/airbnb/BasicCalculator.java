package airbnb;

import java.util.Stack;

public class BasicCalculator {
    public static int calculate(String s) {
        int result = 0;
        if (s == null || s.length() == 0) {
            return result;
        }
        Stack<Integer> stack = new Stack<>();

        int sign = 1, n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                int num = c - '0';
                while (i + 1 < n && Character.isDigit(s.charAt(i + 1))) {
                    num = num * 10 + s.charAt(i + 1) - '0';
                    i++;
                }
                result += sign * num;
            } else if (c == '+') {
                sign = 1;
            } else if (c == '-') {
                sign = -1;
            } else if (c == '(') {
                stack.push(result);
                stack.push(sign);
                sign = 1;
                result = 0;
            } else if (c == ')') {
                result = result * stack.pop() + stack.pop();
                sign = 1;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int result = calculate("1 + 463 - (1    -12+8)");
        System.out.println(result);
    }
}
