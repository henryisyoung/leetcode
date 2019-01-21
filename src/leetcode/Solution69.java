package leetcode;

public class Solution69 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution69 t = new Solution69();
		int k = 8;
		System.out.println("asd"+"as");
	}
    public int mySqrt(int x) {
	    if (x < 0)
	        return -1;
	    if (x <= 1)
	        return x;
	    int low = 1, high = x;
	    while (low + 1 <high) {
	        int mid = low + (high - low) / 2;
	        if (mid == x / mid)
	            return mid;
	        else if (mid < x / mid)
	            low = mid;
	        else
	            high = mid;
	    }
	    return low;
	    
	}
}
