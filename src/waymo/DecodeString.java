package waymo;

import java.util.Stack;
// leetcode 394 decode string
public class DecodeString {
    public String decodeString(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }

        Stack<Integer> count = new Stack<>();
        Stack<String> stack = new Stack<>();
        stack.push("");
        int i = 0, n = s.length();

        while (i < n) {
            if (Character.isDigit(s.charAt(i))) {
                int val = 0;
                while (i < n && Character.isDigit(s.charAt(i))) {
                    val = val * 10 + (s.charAt(i) - '0');
                    i++;
                }
                count.push(val);
            } else if (s.charAt(i) == '[') {
                stack.push("");
                i++;
            } else if (s.charAt(i) == ']') {
                int val = count.pop();
                String str = stack.pop();
                StringBuilder sb = new StringBuilder();
                while (val > 0) {
                    sb.append(str);
                    val--;
                }
                stack.push(stack.pop() + sb.toString());
                i++;
            } else {
                String str = stack.pop() + s.charAt(i);
                stack.add(str);
                i++;
            }
        }
        return stack.pop();
    }
}

