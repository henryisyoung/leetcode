package leetcode.solution;
import java.util.*;
public class Solution150 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] tokens = {"4", "13", "5", "/", "+"};
		Solution150 t = new Solution150();
		String set = "+-*/";
	}
	
    public int evalRPN(String[] tokens) {
        if(tokens == null || tokens.length == 0) return 0;
        Stack<Integer> stack = new Stack<Integer>();
        String set = "+-*/";
        
        for(String str : tokens){
        	if(set.contains(str)){
        		if(stack.size() < 2){
        			return 0;
        		}else{
    				int a1 = stack.pop();
    				int a2 = stack.pop();
        			if(str.equals("+")){
        				stack.push(a1 + a2);
        			}else if(str.equals("-")){
        				stack.push(a2 - a1);
        			}else if(str.equals("/")){
        				stack.push(a2 / a1);
        			}else{
        				stack.push(a1 * a2);
        			}
        		}
        	}else{
        		stack.push(Integer.parseInt(str));
        	}
        }
        return stack.peek();
    }
}
