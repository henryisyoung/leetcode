package facebook;

import java.util.Stack;

public class ValidParentheses {
    public static boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        for(int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if(c == '(' || c == '[' || c == '{'){
                stack.push(c);
            }else{
                if(stack.isEmpty()){
                    return false;
                }
                char cc = stack.pop();
                if(c == ')' && cc != '(') return false;
                if(c == ']' && cc != '[') return false;
                if(c == '}' && cc != '{') return false;
            }
        }
        return stack.isEmpty();
    }


    public static void main(String[] args) {
        System.out.println(isValid("()[]{}"));
    }
}
