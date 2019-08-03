package pinterest;

import java.util.Stack;

public class BasicCalculator {
    public static int calculate(String s) {
        if (s == null || s.length() == 0) return 0;
        Stack<Integer> stack = new Stack<>();
        int result = 0;
        int prev = 1;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(s.charAt(i))) {
                int num = 0;
                while (i < s.length() && Character.isDigit(s.charAt(i))) {
                    num = num * 10 + s.charAt(i) - '0';
                    i++;
                }
                i--;
                result = result + prev * num;
            }
            if (c == '+') {
                prev = 1;
            }
            if (c == '-') {
                prev = -1;
            }
            if (c == '(') {
                stack.push(result);
                stack.push(prev);
                prev = 1;
                result = 0;
            }
            if (c == ')') {
                result = stack.pop() * result + stack.pop();
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
