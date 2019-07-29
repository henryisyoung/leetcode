package pinterest;

import java.util.Stack;

public class BasicCalculator {
    public static int calculate(String s) {
        if (s == null || s.length() == 0) return 0;
        Stack<Integer> stack = new Stack<>();
        int prev = 1, result = 0, n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                int num = 0;
                while (i < n && Character.isDigit(s.charAt(i))) {
                    num = num * 10 + s.charAt(i++) - '0';
                }
                i--;
                result += prev * num;
            } else if (c == '+') {
                prev = 1;
            } else if (c == '-') {
                prev = -1;
            } else if (c == '(') {
                stack.push(prev);
                stack.push(result);
                result = 0;
                prev = 1;
            } else if (c == ')') {
                result = stack.pop() + stack.pop() * result;
                prev = 1;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String s = "(1+(4+5+2)-3)+(6+8)";
        System.out.println(calculate(s));
    }
}
