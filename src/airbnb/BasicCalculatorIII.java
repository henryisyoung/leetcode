package airbnb;

import java.util.Stack;

public class BasicCalculatorIII {
    public int calculate(String s) {
        s = s.replaceAll(" ", "");
        if (s.length() == 0) return 0;
        Stack<Integer> stack = new Stack<>();
        char sign = '+';
        int res = 0, pre = 0, i = 0;
        while (i < s.length()) {
            char ch = s.charAt(i);
            //consecutive digits as a number, save in `pre`
            if (Character.isDigit(ch)) {
                pre = pre*10+(ch-'0');
            }
            //recursively calculate results in parentheses
            if (ch == '(') {
                int j = findClosing(s.substring(i));
                pre = calculate(s.substring(i+1, i+j));
                i += j;
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
        while (!stack.isEmpty()) res += stack.pop();
        return res;
    }

    //Eliminate all "()" pairs, calculate the result in between and save in `pre`
    private int findClosing(String s) {
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

    public int calculate2(String s) {
        int op1 = 1, op2 = 1;
        int val1 = 0, val2 = 1;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                int num = c - '0';
                while (i + 1 < s.length() && Character.isDigit(s.charAt(i + 1))) {
                    num = num * 10 + (s.charAt(i + 1) - '0');
                    i++;
                }

                val2 = op2 == 1 ? val2 * num : val2 / num;
            }else if (c == '(') {
                int cur = i;
                int count = 0;
                while (i < s.length()) {
                    char ch = s.charAt(i);
                    if (ch == '(') count++;
                    if (ch == ')') count--;
                    if (count == 0) break;
                    i++;
                }

                int num = calculate2(s.substring(cur + 1,i));
                val2 = op2 == 1 ? val2 * num : val2 / num;

            }else if (c == '+' || c == '-') {
                val1 = val1 + op1 * val2;
                op1 = c == '+' ? 1 : -1;
                op2 = 1;
                val2 = 1;
            }else if (c == '*' || c == '/') {
                op2 = c == '*' ? 1 : -1;
            }
        }

        return val1 + op1 * val2;
    }
}
