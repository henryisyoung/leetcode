package leetcode.solution;

public class Solution55 {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution55 t = new Solution55();
		int[] nums={2,3,1,1,4};
		int[] nums2={3,2,1,1,4};
		System.out.println(t.canJump(nums2));
		
	}
    public boolean canJump(int[] nums) {
    	int len=nums.length;
    	int[] jump = new int[len];
    	jump[0]=0;
    	for(int i=1;i<len;i++){
    		jump[i] = Math.max(jump[i-1], nums[i-1])-1;
    		if(jump[i]<0) return false;
    	}
    	return jump[len-1]>=0;
    }
    
    public boolean canJump2(int[] nums) {
    	int maxcover=0;
    	for(int i=0;i<nums.length&&i<=maxcover;i++){
    		if(nums[i]+i>maxcover){
    			maxcover=nums[i]+i;
    		}
    		if(maxcover>=nums.length-1) return true;
    	}
    	return false;
    }
    
    public boolean canJumpDP(int[] nums) {
    	if(nums.length < 2 || nums == null) return true;
    	int len = nums.length;
    	boolean[] f = new boolean[len];
    	f[0] = true;
    	for(int i = 1; i < len; i++){
    		f[i] = false;
    		for(int j = 0; j < i; j++){
    			if(f[j] && j + nums[j] >= i){
    				f[i] = true;
    			}
    		}
    	}
    	return f[len-1];
    }
}
