package leetcode.dataStructrue.stack;

import java.util.Stack;

//Examples = abc3[a] return abcaaa
//        s = 3[abc] return abcabcabc
//        s = 4[ac]dy, return acacacacdy
//        s = 3[2[ad]3[pf]]xyz, return adadpfpfpfadadpfpfpfadadpfpfpfxyz
public class ExpressionExpand2 {
    public String expressionExpand(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        Stack<Object> stack = new Stack<>();
        int number = 0;
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                number = number * 10 + (c - '0');
            } else if (c == '[') {
                stack.push(number);
                number = 0;
            } else if (c == ']') {
                String str = popStack(stack);
                int count = (int) stack.pop();
                while (count > 0) {
                    stack.push(str);
                    count--;
                }
            } else {
                stack.push(String.valueOf(c));
            }
        }
        return popStack(stack);
    }

    private String popStack(Stack<Object> stack) {
        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty() && stack.peek() instanceof  String) {
            sb.insert(0, stack.pop());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        ExpressionExpand2 expand = new ExpressionExpand2();
        String result = expand.expressionExpand("abc3[q]");
        System.out.println(result);
    }
}
