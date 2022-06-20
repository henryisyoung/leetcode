package datastructure.array;

import java.util.Arrays;

public class ThreeSumSmaller {
    public static int threeSumSmaller(int[] nums, int target) {
        if (nums == null || nums.length < 3) return 0;
        int len = nums.length;
        Arrays.sort(nums);
        System.out.println(Arrays.toString(nums));
        int result = 0;
        for (int i = 0; i < len - 2; i++) {
            int j = i + 1, k = len - 1;
            while (j < k) {
                int sum = nums[i] + nums[j] + nums[k];
                if (sum < target) {
                    result += k - j;
                    j++;
                } else {
                    k--;
                }

            }
        }

        return result;
    }

    public static void main(String[] args) {
        int[] nums = {-2,0,1,3};
        int target = 2;
        System.out.println(threeSumSmaller(nums, target));
    }
}
