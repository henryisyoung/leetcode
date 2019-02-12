package leetcode.twoPointer;

import java.util.Arrays;

public class ThreeSumClosest {
    public int threeSumClosest(int[] nums, int target) {
        int result = 0;
        int min = Integer.MAX_VALUE;
        Arrays.sort(nums);
        int n = nums.length;
        for (int i = 0; i < n - 2; i++) {
            int j = i + 1, k = n - 1;
            while(j<k){
                int sum = nums[i] + nums[j] + nums[k];
                if(sum == target) {
                    return target;
                }else {
                    if(Math.abs(target - sum) < min) {
                        min = Math.abs(target - sum);
                        result = sum;

                    }
                    if(sum > target) {
                        k--;
                        while (k > j && nums[k] == nums[k + 1]) {
                            k--;
                        }
                    } else {
                        j++;
                        while (k > j && nums[j] == nums[j - 1]) {
                            j++;
                        }
                    }
                }
                while (i < n - 2 && nums[i] == nums[i + 1]) {
                    i++;
                }
            }
        }
        return result;
    }
    public int threeSumClosest2(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        Arrays.sort(nums);
        int n = nums.length;
        int max = Integer.MAX_VALUE, result = 0;
        for (int i = 0; i < n - 2; i++) {
            int j = i + 1, k = n - 1;
            while (j < k) {
                int sum = nums[i] + nums[j] + nums[k];
                if (sum == target) {
                    return sum;
                } else {
                    if (Math.abs(sum - target) < max) {
                        result = sum;
                        max = Math.abs(sum - target);
                    }
                    if (sum > target) {
                        k--;
                    } else {
                        j++;
                    }
                }
            }
        }
        return result;
    }
}
