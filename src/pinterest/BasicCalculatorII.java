package pinterest;

import java.util.Stack;

public class BasicCalculatorII {
    public int calculate(String s) {
        s = s.replaceAll(" ", "");
        Stack<Integer> stack = new Stack<>();
        int prev = 0, n = s.length();
        char prevSign = '+';
        for (int i = 0; i < n; i++){
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                prev = prev * 10 + c - '0';
            }
            if (i == n - 1 || !Character.isDigit(c)) {
                switch (prevSign) {
                    case '+':
                        stack.push(prev); break;
                    case '-':
                        stack.push(prev * -1); break;
                    case '*':
                        stack.push(stack.pop() * prev); break;
                    case '/':
                        stack.push(stack.pop() / prev); break;
                }
                prev = 0;
                prevSign = c;
            }
        }
        int result = 0;
        while (!stack.isEmpty()) {
            result += stack.pop();
        }
        return result;
    }

    private int findNextBrace(String s, int i) {
        int level = 1, start = i + 1;
        for (; start < s.length(); start++) {
            char c = s.charAt(start);
            if (c == ')') {
                level--;
            }
            if (c == '(') {
                level++;
            }
            if (level == 0) {
                return start;
            }
        }
        return -1;
    }
}
