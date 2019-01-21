package leetcode;

import java.util.HashMap;

public class Solution5 {
	public static void main(String[] args) {
		String s = "abababababababababababababababababababababababababababababababababa"
				+ "babababababababababababababababababababababababababababababababababababa"
				+ "bababababababababababababababababababababababababababababababababababababa"
				+ "bababababababababababababababababababababababababababababababababababababab"
				+ "abababababababababababababababababababababababababababababababababababababab"
				+ "ababababababababababababababababababababababababababababababababababababab"
				+ "abababababababababababababababababababababababababababababababababababababa"
				+ "babababababababababababababababababababababababababababababababababababab"
				+ "ababababababababababababababababababababababababababababababababababababab"
				+ "abababababababababababababababababababababababababababababababababababa"
				+ "babababababababababababababababababababababababababababababababababababa"
				+ "babababababababababababababababababababababababababababababababababababa"
				+ "bababababababababababababababababababababababababababababababababababab"
				+ "ababababababababababababababababababababababababababa";
		String s1 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		Solution5 t = new Solution5();
		System.out.println(t.longestPalindrome(s));
	}
	 int subBegin = 0;
	 int maxLength = 0;
	
    public String longestPalindrome(String s) {
    	if(s.length()<2|| s==null) return s;
    	char[] arr = s.toCharArray();
    	for(int i=0;i<arr.length-1;i++){
    		checkthisPoint(arr,i,i);
    		checkthisPoint(arr,i,i+1);
    	}
		return s.substring(subBegin,subBegin+maxLength);
    	
    }

	private void checkthisPoint(char[] arr, int left, int right) {
		while(left>=0 && right<arr.length && arr[left]==arr[right]){
			left--;
			right++;
		}
		if(right-left-1>maxLength){
			subBegin = left + 1;
			maxLength = right-left-1;
		}
		
	}
}
