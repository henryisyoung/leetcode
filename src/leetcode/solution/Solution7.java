package leetcode.solution;

public class Solution7 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution7 t = new Solution7();
		int a = t.reverse(-31200);
		System.out.println(a);
	}
	
	public int reverse(int m){
		long sum = 0;
		while(m != 0){
			sum = m%10 + 10*sum;
			m= m/10;
		}
		if(sum>Integer.MAX_VALUE ||sum<Integer.MIN_VALUE) return 0;
		return (int) sum;
	}

}
