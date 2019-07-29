package pinterest;

import java.util.Stack;

public class BasicCalculatorII {
    public static int calculate(String s) {
        if (s == null || s.length() == 0) return 0;
        s = s.replaceAll(" ", "");
        Stack<Integer> stack = new Stack<>();
        int prev = 0, n = s.length();
        char prevSign = '+';
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                prev = prev * 10 + c - '0';
            }
            if (i == n - 1 || !Character.isDigit(c)) {
                if (prevSign == '+') {
                    stack.push(prev);
                } else if (prevSign == '-') {
                    stack.push(-1 * prev);
                } else if (prevSign == '*') {
                    stack.push(stack.pop() * prev);
                } else if (prevSign == '/') {
                    stack.push(stack.pop() / prev);
                }
                prev = 0;
                prevSign = c;
            }
        }
        int sum = 0;
        while (!stack.isEmpty()){
            sum += stack.pop();
        }
        return sum;
    }

    public static void main(String[] args) {
        String s = " 3+5 / 2 ";
        System.out.println(calculate(s));
    }
}
