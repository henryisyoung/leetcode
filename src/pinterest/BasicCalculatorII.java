package pinterest;

import java.util.Stack;

public class BasicCalculatorII {
    public int calculate(String s) {
        s = s.replaceAll(" ", "");
        if (s.length() == 0) {
            return 0;
        }
        Stack<Integer> stack = new Stack<>();
        char sign = '+';
        int res = 0, pre = 0, i = 0;

        while (i < s.length()) {
            char ch = s.charAt(i);
            //consecutive digits as a number, save in `pre`
            if (Character.isDigit(ch)) {
                pre = pre * 10+ (ch - '0');
            }
            //for new signs, calculate with existing number/sign, then update number/sign
            if (i == s.length() - 1 || !Character.isDigit(ch)) {
                switch (sign) {
                    case '+':
                        stack.push(pre); break;
                    case '-':
                        stack.push(-pre); break;
                    case '*':
                        stack.push(stack.pop()*pre); break;
                    case '/':
                        stack.push(stack.pop()/pre); break;
                }
                pre = 0;
                sign = ch;
            }
            i++;
        }
        while (!stack.isEmpty()) {
            res += stack.pop();
        }
        return res;
    }
}
