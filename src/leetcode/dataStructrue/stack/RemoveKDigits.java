package leetcode.dataStructrue.stack;

import java.util.*;

public class RemoveKDigits {
    public static String removeKdigits(String num, int k) {
        if(num == null || num.length() <= k) {
            return "0";
        }
        Stack<Integer> stack = new Stack<>();
        int i = 0, n = num.length();
        while (i < n) {
            int cur = num.charAt(i++) - '0';
            while (!stack.isEmpty() && k > 0 && stack.peek() >= cur) {
                stack.pop();
                k--;
            }
            if (cur == 0 && stack.isEmpty()) {
                continue;
            }
            stack.push(cur);
        }
        String result  = "";
        while (k > 0 && !stack.isEmpty()) {
            stack.pop();
            k--;
        }
        while (!stack.isEmpty()) {
            result = stack.pop() + result;
        }
        return result == "" ? "0" : result;
    }

    public static void main(String[] args) {
        int k = 3, k2 = 2, k3 = 1;
        String num = "1432219", num2 = "10", num3 = "10200";

        System.out.println(removeKdigits(num, k));
        System.out.println(removeKdigits(num2, k2));
        System.out.println(removeKdigits(num3, k3));
    }
}
