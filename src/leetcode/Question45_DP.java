package leetcode;

public class Question45_DP {
    public int jump(int[] nums) {
        if(nums == null || nums.length < 2) return 0;
        int n = nums.length;
        int[] f = new int[n];
        for(int i = 1; i < n; i++){
            f[i] = Integer.MAX_VALUE;
        }
        for (int i = 1; i < n; i++){
            for (int j = 0; j < i; j++){
                if(f[j] != Integer.MAX_VALUE && nums[j] + j >= i){
                    f[i] = Math.min(f[j] + 1, f[i]);
                }
            }
        }
        return f[n-1];
    }
}
