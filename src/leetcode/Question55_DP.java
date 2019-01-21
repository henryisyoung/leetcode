package leetcode;

public class Question55_DP {
    public boolean canJump(int[] nums) {
        if(nums == null || nums.length < 2) return true;
        int n = nums.length;
        boolean[] f = new boolean[n];
        f[0] = true;
        for(int i = 1; i < n; i++ ){
            for (int j = 0; j < i; j++){
                if(f[j] && nums[j] + j >= i){
                    f[i] = true;
                    break;
                }
            }
        }
        return f[n-1];
    }
}
