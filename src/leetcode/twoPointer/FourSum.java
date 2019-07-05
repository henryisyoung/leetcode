package leetcode.twoPointer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FourSum {
    public static List<List<Integer>> fourSum(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        int n = nums.length;
        for (int i = 0; i <= n - 4; i++) {
            for (int j = i + 1; j <= n - 3; j++) {
                int k = j + 1, t = n - 1;
                while (k < t) {
                    if (nums[i] + nums[j] + nums[k] + nums[t] == target) {
                        result.add(Arrays.asList(nums[i], nums[j], nums[k], nums[t]));
                        k++;
                        do{t--;}while(k < t && nums[t] == nums[t+1]);
                    } else if (nums[i] + nums[j] + nums[k] + nums[t] > target) {
                        t--;
                        while (t > k && nums[t] == nums[t + 1]) {
                            t--;
                        }
                    } else {
                        k++;
                        while (t > k && nums[k] == nums[k - 1]) {
                            k++;
                        }
                    }
                }
                while (j <= n - 3 &&  nums[j] == nums[j + 1]) {
                   j++;
                }
            }
            while (i <= n - 4 && nums[i] == nums[i + 1]) {
                i++;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[] nums = {1,0,-1,0,-2,2};
        int k = 0;
        List<List<Integer>> result = fourSum(nums, k);
        System.out.println(result.toString());
    }
}
