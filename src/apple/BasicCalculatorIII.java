package apple;

import java.util.Stack;

public class BasicCalculatorIII {
    public static int calculate(String s) {
        if (s == null || s.length() == 0) return 0;
        s = s.replaceAll(" ", "");
        Stack<Integer> stack = new Stack<>();
        char sign = '+';
        int n = s.length();
        int prev = 0;
        for (int i = 0; i < s.length(); i++) {
            char cur = s.charAt(i);
            if (Character.isDigit(cur)) {
                prev = prev * 10 + cur - '0';
            }
            if (cur == '(') {
                int pos = findRight(i, s);
                prev = calculate(s.substring(i + 1, pos));
                i = pos;
            }
            if (!Character.isDigit(cur) || i == n - 1) {
                if (sign == '+') {
                    stack.push(prev);
                } else if (sign == '-') {
                    stack.push(-prev);
                } else if (sign == '*') {
                    stack.push(stack.pop() * prev);
                } else if (sign == '/') {
                    stack.push(stack.pop() / prev);
                }
                sign = cur;
                prev = 0;
            }
        }
        int sum = 0;
        while (!stack.isEmpty()) {
            sum += stack.pop();
        }
        return sum;
    }

    private static int findRight(int start, String s) {
        int left = 1;

        for (int i = start + 1; i < s.length(); i++) {
            if (s.charAt(i) == '(') left++;
            else if (s.charAt(i) == ')') left--;

            if (left == 0) return i;
        }
        return -1;
    }

    public static void main(String[] args) {
        System.out.println(calculate(" 3+5 / 2 + 4/   2"));
    }
}
