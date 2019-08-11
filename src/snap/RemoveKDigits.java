package snap;

import java.util.Stack;

public class RemoveKDigits {
    public String removeKdigits(String num, int k) {
        if(num == null || num.length() <= k) {
            return "0";
        }
        Stack<Integer> stack = new Stack<>();
        int i = 0;
        while (i < num.length()) {
            int cur = num.charAt(i++) - '0';
            while (k > 0 && !stack.isEmpty() && cur < stack.peek()) {
                k--;
                stack.pop();
            }
            if (cur == 0 && stack.isEmpty()) continue;
            stack.push(cur);

        }
        while (!stack.isEmpty() && k > 0) {
            stack.pop();
            k--;
        }
        if (stack.isEmpty()) return "";
        String result = "";
        while (!stack.isEmpty()) {
            result = stack.pop() + result;
        }
        return result.length() == 0 ? "0" : result;
    }
}
