package leetcode;

public class Solution45 {

	public static void main(String[] args) {
		Solution45 t = new Solution45();
		int[] nums = {2,3,1,1,4};
		System.out.println(t.jump(nums));

	}
    public int jump(int[] nums) {
        int start=0,end=0,count=0,max=0,len=nums.length;
        if(len==1) return 0;
        while(end<len){
        	count++;
        	for(int i=start;i<=end;i++){
        		if(nums[i]+i>=len-1){
        			return count;
        		}
        		if(nums[i]+i>max){
        			max=nums[i]+i;
        		}
        	}
        	start=end+1;
        	end=max;
        }
        return count;
    }
    public int jumpDP(int[] nums) {
    	int n = nums.length;
    	int[] f = new int[n];
    	f[0] = 0;
    	for(int i = 1; i < n; i++){
    		f[i] = Integer.MAX_VALUE;
    		for(int j = 0; j < i; j++){
    			if(f[j] != Integer.MAX_VALUE && j + nums[j] >= i){
    				f[i] = Math.min(f[j] + 1, f[i]) ;
    			}
    		}
    		
    	}
    	return f[n-1];
    }
}
