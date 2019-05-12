package pinterest;

import java.util.Stack;

public class BasicCalculatorIII {
    public static int calculate(String s) {
        s = s.replaceAll(" ", "");
        if (s.length() == 0) {
            return 0;
        }
        int result = 0, n = s.length();
        int prev = 0;
        char prevSign = '+';
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < n; i++){
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                prev = prev * 10 + c - '0';
            }
            if (c == '(') {
                int len = findClosing(s.substring(i));
                prev = calculate(s.substring(i + 1, i + len));
                i = i + len;
            }
            if (i == n - 1 || !Character.isDigit(c)) {
                switch (prevSign){
                    case '+':
                        stack.push(prev); break;
                    case '-':
                        stack.push(prev * -1); break;
                    case '*':
                        stack.push(stack.pop() * prev); break;
                    case '/':
                        stack.push(stack.pop() / prev); break;
                }
                prevSign = c;
                prev = 0;
            }
        }
        while (!stack.isEmpty()) {
            result += stack.pop();
        }
        return result;
    }

    private static int findClosing(String s) {
        int level = 0, i = 0;
        for (i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') level++;
            else if (s.charAt(i) == ')') {
                level--;
                if (level == 0) break;
            } else continue;
        }
        return i;
    }

    public static void main(String[] args) {
        String s = "2*(5+5*2)/3+(6/2+8)";
        System.out.println(calculate(s));
    }
}
