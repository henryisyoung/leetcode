package pinterest;

import java.util.Stack;

public class BasicCalculatorII2 {
    public static int calculate(String s) {
        s = s.replace(" ", "");
        int prev = 0, n = s.length();
        char prevSign = '+';
        int result = 0;
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                prev = prev * 10 + c - '0';
            }
            if (i == n - 1 || !Character.isDigit(c)) {
                switch (prevSign){
                    case '+':
                        stack.push(prev); break;
                    case '-':
                        stack.push(-1 * prev); break;
                    case '*':
                        stack.push(stack.pop() * prev); break;
                    case '/':
                        stack.push(stack.pop() / prev); break;
                }
                prev = 0;
                prevSign = c;
            }
        }
        while (!stack.isEmpty()) {
            result += stack.pop();
        }
        return result;
    }

    public static void main(String[] args) {
        String s = " 3+5 / 2 ";
        System.out.println(calculate(s));
    }
}
