package leetcode.solution;

import java.util.Stack;

public class Solution224 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
    public int calculate(String s) {  
        Stack<Integer> stack = new Stack<>();  
        stack.push(1);  
        stack.push(1);  
        int res = 0;  
        for (int i = 0; i < s.length(); i++) {  
            char c = s.charAt(i);  
            if (Character.isDigit(c)) {  
                int num = c - '0';  
                int j = i + 1;  
                while (j < s.length() && Character.isDigit(s.charAt(j))) {  
                    num = 10 * num + (s.charAt(j) - '0');  
                    j++;  
                }  
                res += stack.pop() * num;  
                i = j - 1;  
            } else if (c == '+' || c == '(') {  
                stack.push(stack.peek());  
            } else if (c == '-') {  
                stack.push(-1 * stack.peek());  
            } else if (c == ')') {  
                stack.pop();  
            }  
        }  
        return res;  
    }  
}
