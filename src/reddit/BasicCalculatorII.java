package reddit;

import java.util.Stack;

public class BasicCalculatorII {
    public static int calculate(String s) {
        Stack<Integer> stack = new Stack<>();
        int prev = 0;
        char sign = '+';
        s = s.replaceAll(" ", "").trim();
        int i = 0, n = s.length();

        while(i < n) {
            char c = s.charAt(i);
            if(Character.isDigit(c)) {
                prev = prev * 10 + c - '0';
            }
            if(!Character.isDigit(c) || i == n - 1) {
                if(sign == '+') {
                    stack.push(prev);
                } else if(sign == '-') {
                    stack.push(-prev);
                } else if(sign == '*') {
                    stack.push(stack.pop() * prev);
                } else if(sign == '/') {
                    stack.push(stack.pop() / prev);
                }
                prev = 0;
                sign = c;
            }
            i++;
        }
        int sum = 0;
        while(!stack.isEmpty()) {
            sum += stack.pop();
        }
        return sum;
    }

    public static void main(String[] args) {
        String s = " 3/2 ";
        System.out.println(calculate(s));
    }
}
