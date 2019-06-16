package leetcode.solution;

public class Solution214 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "aacecaaa";
		Solution214 t = new Solution214();
		System.out.println(t.shortestPalindrome(s));
	}
	public String shortestPalindrome(String s) {
		if(s == null || s.length() == 0){
			return s;
		}
		int j = 0;
		for(int i = s.length() - 1; i >= 0; i--){
			if(s.charAt(i) == s.charAt(j)){
				j++;
			}
		}
		if(j == s.length()){
			return s;
		}
        String suffix = s.substring(j); // 后缀不能够匹配的字符串
        String prefix = new StringBuilder(suffix).reverse().toString(); // 前面补充prefix让他和suffix回文匹配
        String mid = shortestPalindrome(s.substring(0, j)); //递归调用找 [0,j]要最少可以补充多少个字符让他回文
        String ans = prefix + mid  + suffix;
        return  ans;
	}
}
