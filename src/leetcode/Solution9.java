package leetcode;

public class Solution9 {
	public static void main(String[] args) {
		Solution9 t = new Solution9();
		System.out.println(t.isPalindrome(20));
	}
    public boolean isPalindrome(int x) {
    	if(x<0) return false; // assume negative number NG! ASK Interviewer
        int m = x;
        int sum = 0;
		while(m != 0){
			sum = m%10 + 10*sum;
			m= m/10;
		}
        return sum-x==0;
    }
    public boolean isPalindrome2(int x) {
        if (x < 0) return false;
        int y = 0, x1 = 1;   // go half
        while (x / x1 != 0) {
            y = y * 10 + x % 10;
            x /= 10;
            x1 *= 10;
        }
        return y < 10 * x ? y == x : y / 10 == x;
    }
    


}
