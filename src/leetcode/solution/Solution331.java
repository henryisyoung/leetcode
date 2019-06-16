package leetcode.solution;
import java.util.*;
public class Solution331 {
	public static void main(String[] args) {
		Solution331 t = new Solution331();
		String preorder = "1,#,#,#,#";
		System.out.println(t.isValidSerialization(preorder)); 
	}
    public boolean isValidSerialization(String preorder) {
        if(preorder == null || preorder.length() == 0) return true;
        Stack<String> stack = new Stack<String>();
        String[] arr = preorder.split(",");
        for(String str : arr){
        	
        	while(!stack.isEmpty() && str.equals("#") && stack.peek().equals(str)){
        		stack.pop();
        		if(stack.isEmpty()){  /// "1,#,#,#,#"
        			return false;
        		}
        		stack.pop();
        	}
        	stack.push(str);
        }
        return stack.pop().equals("#") && stack.isEmpty();
     }
}
