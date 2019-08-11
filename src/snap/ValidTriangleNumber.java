package snap;

import java.util.Arrays;

public class ValidTriangleNumber {
    public int triangleNumber(int[] nums) {
        if (nums == null || nums.length < 3) return 0;
        Arrays.sort(nums);
        int count  = 0;
        int n = nums.length;
        for (int i = n - 1; i >= 0; i--) {
            int c = nums[i];
            int j = 0, k = i - 1;
            while (j < k) {
                if (nums[j] + nums[k] > c) {
                    count += k - j;
                    k--;
                } else {
                    j++;
                }
            }
        }
        return count;
    }

    public static void main(String[] args) {
        int[] nums = {2,2,3,4};
        ValidTriangleNumber solver = new ValidTriangleNumber();
        System.out.println( solver.triangleNumber(nums));
    }
}
