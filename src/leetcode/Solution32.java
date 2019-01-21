package leetcode;
import java.util.*;
public class Solution32 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution32 t = new Solution32();
		String s = "(())";
		System.out.println(t.longestValidParentheses2(s));
	}
	
    public int longestValidParentheses(String s) {
    	if(s.length()<2) return 0;
    	int max=0,temp=0;
        Stack stack = new Stack();
        int[] data = new int[s.length()];
        for(int i=0;i<s.length();i++){
        	char c = s.charAt(i);
        	if(c=='('){
        		stack.push(i);
        	}else{
        		if(!stack.isEmpty()){
        			data[i] = 1;
        			data[(int) stack.pop()] = 1;
        		}
        	}
        }
        for(int i:data){
        	if(i==1){
        		temp++;
        	}else{
        		max=Math.max(temp, max);
        		temp=0;
        	}
        }
        return Math.max(temp, max);
    }
    public int longestValidParentheses2(String s) {
    	if(s.length()<2) return 0;
    	int n = s.length();
    	int[] dp = new int[n + 1];
    	int max = 0;
    	for(int i = 1; i <= n; i++){
    		int j = i - 2 - dp[i - 1];
    		if(j < 0 || s.charAt(i - 1) == '(' || s.charAt(j) == ')'){
    			dp[i] = 0;
    		}else{
    			dp[i] = dp[i-1] + 2 + dp[j];
    		}
    		max = Math.max(max, dp[i]);
    	}
    	return max;
    }

}
