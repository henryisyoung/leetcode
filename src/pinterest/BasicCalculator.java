package pinterest;

import java.util.Stack;

public class BasicCalculator {
    public int calculate(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        int result = 0, sign = 1, n = s.length();
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < n; i++){
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                int num = 0;
                while (i < n && Character.isDigit(s.charAt(i))) {
                    num = num * 10 + s.charAt(i) - '0';
                    i++;
                }
                i--;
                result = result + sign * num;
            }
            if (c == '+') {
                sign = 1;
            }
            if (c == '-') {
                sign = -1;
            }
            if (c == '(') {
                stack.push(sign);
                stack.push(result);
                result = 0;
                sign = 1;
            }
            if (c == ')') {
                result = stack.pop() + stack.pop() * result;
                sign = 1;
            }
        }
        return result;
    }
}
