package recovery;

import java.util.Stack;

public class ValidParentheses {
    public boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (c == ')' || c == '}' || c == ']') {
                if (stack.isEmpty()) return false;
                char left = stack.pop();
                if (c == ')' && left != '(') return false;
                if (c == ']' && left != '[') return false;
                if (c == '}' && left != '{') return false;
            } else {
                stack.add(c);
            }
        }

        return stack.isEmpty();
    }
}
