package leetcode.solution;

public class Solution287 {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution287 t = new Solution287();
		int[] nums = {4,3,3,2,1};
		System.out.println(t.findDuplicate(nums));
	}
	
    public int findDuplicate(int[] nums) {
        int low = 1, high = nums.length - 1;
        while (low + 1 < high) {
            int mid = low + (high - low)/2;
            int cnt = 0;
            for (Integer a : nums) {
            	if(a <= mid){
            		cnt++;
            	}
            }
            if(cnt <= mid){
            	low = mid + 1;
            }else{
            	high = mid - 1;
            }
        }
        return low;
    }
}
