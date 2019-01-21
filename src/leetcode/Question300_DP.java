package leetcode;

public class Question300_DP {
    public int lengthOfLIS(int[] nums) {
        if(nums == null || nums.length == 0) return 0;
        int n = nums.length;
        int[] f = new int[n];
        for (int i=0;i<n;i++) f[i]=1;
        int max = 1;
        for (int i = 1; i < n; i++){
            for(int j = 0; j < i; j++){
                if(nums[j] < nums[i]){
                    f[i] = Math.max(f[i], f[j]+1);
                }
            }
            max = Math.max(f[i], max);
        }
        return max;
    }
}
