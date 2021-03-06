package airbnb;

import java.util.Stack;

public class BasicCalculator {
    public static int calculate(String s) {
        int result  = 0;
        int sign = 1;
        if (s == null || s.length() == 0) {
            return result;
        }
        int n = s.length();
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < n; i++) {
            if (Character.isDigit(s.charAt(i))) {
                int num = 0;
                while (i < n && Character.isDigit(s.charAt(i))) {
                    num = num * 10 + s.charAt(i) - '0';
                    i++;
                }
                i--;
                result = result + sign * num;
            } else if (s.charAt(i) == '+') {
                sign = 1;
            } else if (s.charAt(i) == '-') {
                sign = -1;
            } else if (s.charAt(i) == '(') {
                stack.push(result);
                stack.push(sign);
                sign = 1;
                result = 0;
            } else if (s.charAt(i) == ')') {
                result = stack.pop() * result + stack.pop();
                sign = 1;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int result = calculate("1 + 463 - (1    -12+8) + 123");
        System.out.println(result);
    }
}
