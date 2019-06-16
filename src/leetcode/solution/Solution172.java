package leetcode.solution;

public class Solution172 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public int trailingZeroes(int n) {
		if(n < 0){
			return 0;
		}
		int count = 0;
		for(int i = 5; n/i >= 5; i *= 5){
			count += n/i;
		}
		return count;
	}
	
	public int trailingZeroes2(int n) {
		if(n < 0){
			return 0;
		}
		int count = 0;
		while(n/5 >= 1){
			count += n/5;
			n /= 5;
		}
		return count;
	}
}
