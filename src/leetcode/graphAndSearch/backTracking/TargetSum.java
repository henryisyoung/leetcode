package leetcode.graphAndSearch.backTracking;

import java.util.*;

public class TargetSum {
    int count = 0;
    public int findTargetSumWays(int[] nums, int S) {
        if(nums == null || nums.length == 0) {
            return 0;
        }
        dfsSearchAll(nums, S, 0, 0);
        return count;
    }

    private void dfsSearchAll(int[] nums, int s, int cur, int pos) {
        if (pos == nums.length ) {
            if (cur == s) count++;
        } else {
            int val = nums[pos];
            dfsSearchAll(nums, s, cur + val, pos + 1);
            dfsSearchAll(nums, s, cur + val * -1, pos + 1);
        }
    }

//    why s = (sum + S)/2, that's coz we basically need to divide the delta (sum - S) into two subsets with equal sum
//            (half will be +ve and half will be -ve). This can also be restated as finding a subset with
//    size = S + (sum - S)/2 = (sum + S)/2
//    ex: for sum = 5 and target = 3, delta = 5 - 3 = 2, we need to divide the remaining 2 into two subsets such that
//    we get +1 in one subset and -1 in the other, or we can say we need to find subsets with sum = 3 + 1 = 4,
//    the other subset with sum = 1 can be turned negative, resulting in total sum = 3
//    https://leetcode.com/problems/target-sum/discuss/308790/Java-solution-beats-100-tranform-question-into-subset-sum
    public int findTargetSumWaysDP(int[] nums, int S) {
        int sum = 0;
        for(int num : nums) sum += num;
        if(sum < S || (sum + S)%2 == 1) return 0;
        int s = (sum + S) / 2;
        int[] dp = new int[s + 1];
        dp[0] = 1;
        for (int n : nums)
            for (int i = s; i >= n; i--)
                dp[i] += dp[i - n];
        return dp[s];
    }

    public int findTargetSumWaysMemo(int[] nums, int S) {
        int maxSum = 0;
        for(int num: nums){
            maxSum += num;
        }

        Integer dp[][] = new Integer[nums.length][maxSum*2 + 1];//since it's possible for our sum (in our recursive call)
        //to be from -maxSum to maxSum. We will intialize an array with a size of 2 times maxSum + 1. Since maximum value is
        //maxSum * 2 (to account for index out of bounds)

        //we will then do a shift upwards to make the sums are positive;

        return helper(nums, 0, S, 0, maxSum ,dp);
    }

    public int findTargetSumWaysDP2(int[] nums, int S) {
        Map<Integer, Integer> dp = new HashMap<>();
        dp.put(0, 1);
        for (int num : nums) {
            Map<Integer, Integer> t = new HashMap<>();
            for (Map.Entry<Integer, Integer> a : dp.entrySet()) {
                int sum = a.getKey(), cnt = a.getValue();
                t.put(sum + num, t.getOrDefault(sum + num, 0) + cnt);
                t.put(sum - num, t.getOrDefault(sum - num, 0) + cnt);
            }
            dp = t;
        }
        return dp.get(S) == null ? 0 : dp.get(S);
    }

    private int helper(int[] nums, int pos, int S, int sum, int maxSum, Integer[][] dp){
        if(nums.length == pos){ //base case is when our index is at the end
            return sum == S ? 1: 0;
        }

        //note we do sum + maxSum since we are trying to make (sum + maxSum) positive
        if(dp[pos][sum + maxSum] == null){
            int positive = helper(nums, pos + 1, S, sum + nums[pos], maxSum, dp); //we choose either to make current value positive/negative
            int negative = helper(nums, pos + 1, S, sum - nums[pos], maxSum, dp);

            dp[pos][sum + maxSum] = positive + negative; //let's cache our result
        }

        return dp[pos][sum + maxSum];//return cached result
    }

    public static void main(String[] args) {
        int[] nums = {1, 1, 1, 1, 1};
        TargetSum solver = new TargetSum();
        System.out.println(solver.findTargetSumWays(nums, 3));
    }
}
