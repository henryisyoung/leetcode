package leetcode;

import java.util.Stack;

public class Solution20 {
	
	public boolean isValid(String s){
		Stack<Character> stack = new Stack<Character>();
		char[] chr = s.toCharArray();
		for(int i=0; i<s.length(); i++){
			if(chr[i]=='(' || chr[i]=='[' || chr[i]=='{'){
				stack.push(chr[i]);
			}else{
				if(stack.isEmpty()) return false;
				else{
					char a = stack.pop();
					char c = chr[i];
					if(a=='(' && c!= ')') return false;
					else if(a=='{' && c!='}') return false;
					else if(a=='[' && c!=']') return false;
			}
			}
			
		}
		return stack.isEmpty();
	}

}
