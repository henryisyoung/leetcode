package leetcode;

import java.util.*;


public class Solution22 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution22 t = new Solution22();
		System.out.println(t.generateParenthesis2(2));
	}
    public List<String> generateParenthesis(int n) {
        return new ArrayList<String>(helper(n));
    }
	private HashSet<String> helper(int n) {
		HashSet<String> newset = new HashSet<String>();;
		if(n==0){ 
			newset.add("");
		}else{
			HashSet<String> set =helper(n-1);
			for(String str:set){
				newset.add("()"+str);
				for(int i=0;i<str.length();i++){
					if(str.charAt(i)=='('){
						newset.add(insertP(i,str));
					}
				}
			}
		}
		return newset;
	}
	
	private String insertP(int i, String str) {
		String left = str.substring(0,i+1);
		String right = str.substring(i+1);
		return left+"()"+right;
	}
	
	public List<String> generateParenthesis2(int n) {
		char[] str = new char[n*2];
		List<String> list = new ArrayList<String>();
		addParen(list,n,n,str,0);
		return list;
	}
	private void addParen(List<String> list, int leftRem, int rightRem, char[] str, int count) {
		if(leftRem<0 || rightRem<leftRem) return;
		if(leftRem==0 && rightRem==0){
			String s = String.copyValueOf(str);
			list.add(s);
		}else{
			if(leftRem>0){
				str[count]='(';
				addParen(list,leftRem-1,rightRem,str,count+1);
			}
			if(leftRem<rightRem){
				str[count]=')';
				addParen(list,leftRem,rightRem-1,str,count+1);
			}
		}
		
	}

}
