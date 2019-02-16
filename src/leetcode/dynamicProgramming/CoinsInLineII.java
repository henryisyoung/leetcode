package leetcode.dynamicProgramming;

public class CoinsInLineII {
    public boolean firstWillWin(int[] values) {
        // write your code here
        if(values == null || values.length == 0){
            return false;
        }

        int n = values.length;
        int[] dp = new int[n + 1];
        boolean[] isVisted = new boolean[n + 1];
        for(int i = 0; i <= n; i++){
            dp[i] = memoSearch(i, dp, isVisted, values, n);
        }
        return 2*dp[n] > sum(values);
    }

    private int sum(int[] values) {
        int sum = 0;
        for(int i : values){
            sum += i;
        }
        return sum;
    }

    private int memoSearch(int i, int[] dp, boolean[] isVisted, int[] values, int n) {
        // TODO Auto-generated method stub
        if(isVisted[i]){
            return dp[i];
        }else if(i == 0){
            isVisted[i] = true;
            return 0;
        }else if(i == 1){
            isVisted[i] = true;
            return values[n - 1];
        }else if(i == 2){
            isVisted[i] = true;
            return values[n - 1] + values[n - 2];
        }else if(i == 3){
            isVisted[i] = true;
            return values[n - 3] + values[n - 2];
        }else{
            dp[i] = Math.max(Math.min(memoSearch(i - 2, dp, isVisted, values, n), memoSearch(i - 3, dp, isVisted, values, n)) + values[n - i],
                    Math.min(memoSearch(i - 3, dp, isVisted, values, n), memoSearch(i - 4, dp, isVisted, values, n)) + values[n - i] + values[n - i + 1]);
            isVisted[i] = true;
            return dp[i];
        }
    }
}
