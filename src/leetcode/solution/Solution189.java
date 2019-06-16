package leetcode.solution;

import java.util.Arrays;

public class Solution189 {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] nums = {1,2,3,4,5,6,7};
		Solution189 t = new Solution189();
		t.rotate(nums, 3);
		System.out.println(Arrays.toString(nums));
	}
	
    public void rotate(int[] nums, int k) {
        if(nums == null){
        	return;
        }
        int n = nums.length;
        if(nums.length < k){
            k %= n;
        }
        reverse(nums, 0, n - k);
        reverse(nums, n - k, n);
        reverse(nums,0, n);
    }
    
    private void reverse(int [] s, int begin, int end) {
        for (int i=0; i<(end-begin)/2; i++) {
        	int temp = s[begin+i];
            s[begin+i] = s[end-i-1];
            s[end-i-1] = temp;
        }
    }
}
