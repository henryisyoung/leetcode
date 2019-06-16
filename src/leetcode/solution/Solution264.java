package leetcode.solution;

public class Solution264 {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution264 t= new Solution264();
		System.out.println(t.nthUglyNumber(11));
	}
	
	 public int nthUglyNumber(int n) {
		 int[] table = new int[n];
		 table[0] = 1;
		 int i = 0, j = 0, k = 0, pos = 1;
		 while(pos < n){
			 int n2 = table[i] * 2, n3 = table[j] * 3, n5 = table[k] * 5;
			 int min = Math.min(n2, n3);
			 min = Math.min(min, n5);
			 if(n2 == min) i++;
			 if(n3 == min) j++;
			 if(n5 == min) k++;
			 if(min > table[pos]){
				 table[pos] = min;
				 pos++;
			 }
		 }
		 
		 return table[n - 1];
	    }
	 
}
