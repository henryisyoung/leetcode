package leetcode;

public class Solution294 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public boolean canWin(String s) {
		int n = s.length();
		for(int i = 0; i < n -1; i++){
			if(s.charAt(i) == s.charAt(i + 1) && s.charAt(i) == '+' &&
					! canWin(s.substring(0, i) + "--" + s.substring(i + 2))){
				return true;
			}
		}
		return false;
	}

}
