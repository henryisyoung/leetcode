package leetcode.solution;

public class Solution53 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution53 t = new Solution53();
		int[] nums={-1,1,-3,4,-1,2,1,-5,4};
		System.out.println(t.maxSubArray(nums));
		
	}
	/***
	O(n)就是一维DP.
	假设A(0, i)区间存在k，使得[k, i]区间是以i结尾区间的最大值， 定义为Max[i], 在这里，当求取Max[i+1]时，
	Max[i+1] = Max[i] + A[i+1],  if (Max[i] + A[i+1] >0)
                = 0, if(Max[i]+A[i+1] <0)，如果和小于零，A[i+1]必为负数，没必要保留，舍弃掉
	然后从左往右扫描，求取Max数字的最大值即为所求。
	 */
    public int maxSubArray(int[] nums) {
        int sum=0, max=Integer.MIN_VALUE;
        for(int i=0;i<nums.length;i++){
        	sum+=nums[i];
        	if(sum>max) max=sum;
        	if(sum<0) sum=0;
        }
        return max;
    }
    
    public int maxSubArray2(int[] nums) {  // binary not O(n)
    	return maxSubArrayHelper(nums,0,nums.length-1,Integer.MIN_VALUE);
    }
	private int maxSubArrayHelper(int[] nums, int left, int right, int maxV) {
		if(left>right) return Integer.MIN_VALUE;
		else{
			int mid = (left+right)/2;
			int lmax=maxSubArrayHelper(nums,left,mid-1,maxV);
			int rmax=maxSubArrayHelper(nums,mid+1,right,maxV);
			maxV=Math.max(lmax, maxV);
			maxV=Math.max(maxV, rmax);
			int sum=0,nlmax=0,nrmax=0;
			for(int i=mid-1;i>=left;i--){
				sum+=nums[i];
				if(sum>nlmax) nlmax=sum;
			}
		    sum=0;
			for(int i=mid+1;i<=right;i++){
				sum+=nums[i];
				if(sum>nrmax) nrmax=sum;
			}
			maxV = Math.max(maxV, nrmax+nums[mid]+nlmax);
			return maxV;
		}
	}
	
	
	// DP
	public int maxSubArray3(int[] nums) {
		if(nums == null || nums.length == 0){
			return 0;
		}
		int[] dp = new int[nums.length];
		dp[0] = nums[0];
		int max = nums[0];
		
		for(int i = 1; i < nums.length; i++){
			if(dp[i - 1] < 0){
				dp[i] = nums[i];
			}else{
				dp[i] = dp[i - 1] + nums[i];
			}
			if(dp[i] > max){
				max = dp[i];
			}
		}
		return max;
	}
}
