package leetcode.TwoPointer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FourSum {
    public List<List<Integer>> fourSum(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        if(nums == null || nums.length < 4) {
            return result;
        }
        int n = nums.length;

        for (int i = 0; i < n - 3; i++) {
            if(4 * nums[i] > target) return result;

            for (int j = i + 1; j < n - 2; j++) {
                int e = j + 1, k = n - 1;

                while (k > e) {
                    int sum = nums[i] + nums[j] + nums[k] + nums[e];
                    if(sum == target) {
                        List<Integer> list = Arrays.asList(nums[i], nums[j], nums[k], nums[e]);
                        result.add(list);
                        e++;
                        k--;
                    } else if (sum > target) {
                        k--;
                        while (e < k && nums[k] == nums[k + 1]) {
                            k--;
                        }
                    } else {
                        e++;
                        while (e < k && nums[e] == nums[e - 1]) {
                            e++;
                        }
                    }
                }
                while (j < n - 2 && nums[j] == nums[j + 1]) {
                    j++;
                }
            }
            while (i < n - 3 && nums[i] == nums[i + 1]) {
                i++;
            }
        }
        return result;
    }
}
