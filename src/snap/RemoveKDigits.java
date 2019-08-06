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
            while (!stack.isEmpty() && k > 0 && cur < stack.peek()) {
                stack.pop();
                k--;
            }
            if (cur == 0 && stack.isEmpty()) continue;
            stack.push(cur);
        }
        while (k > 0 && !stack.isEmpty()) {
            k--;
            stack.pop();
        }
        String result = "";
        while (!stack.isEmpty()) {
            result = stack.pop() + result;
        }
        return result == "" ? "0" : result;
    }
}
