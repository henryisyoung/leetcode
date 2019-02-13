package leetcode.dataStructrue.stack;

import java.util.Stack;

//Examples = abc3[a] return abcaaa
//        s = 3[abc] return abcabcabc
//        s = 4[ac]dy, return acacacacdy
//        s = 3[2[ad]3[pf]]xyz, return adadpfpfpfadadpfpfpfadadpfpfpfxyz
public class ExpressionExpand {
    public String expressionExpand(String s) {
        // Write your code here
        Stack<Object> stack = new Stack<Object>();
        int number = 0;
        for(char c: s.toCharArray()){
            if(Character.isDigit(c)){
                number = number * 10 + (c-'0');
            }else if(c == '['){
                stack.push(Integer.valueOf(number));
                number = 0;
            }else if(c == ']'){
                String temp = popStack(stack);
                Integer count = (Integer)stack.pop();
                while(count > 0){
                    stack.push(temp);
                    count--;
                }
            }else{
                stack.push(String.valueOf(c));
            }
        }
        return popStack(stack);
    }

    String popStack(Stack<Object> stack){
        Stack<String> container = new Stack<String>();
        while(!stack.empty() && (stack.peek() instanceof String)){
            container.push((String)stack.pop());
        }
        StringBuilder sb = new StringBuilder();
        while(!container.empty()){
            sb.append(container.pop());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        ExpressionExpand expand = new ExpressionExpand();
        String result = expand.expressionExpand("3[2[ad]3[pf]]xyz");
        System.out.println(result);
    }
}
