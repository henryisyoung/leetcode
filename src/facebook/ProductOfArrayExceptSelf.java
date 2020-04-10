package facebook;

import java.util.*;

public class ProductOfArrayExceptSelf {
    public static int[] productExceptSelf(int[] nums) {
        if (nums == null || nums.length == 0) return nums;
        int n = nums.length;
        int[] result = new int[n];
        int[] left = new int[n], right = new int[n];
        left[0] = nums[0];
        for (int i = 1; i < n; i++) {
            left[i] = left[i - 1] * nums[i];
        }
        right[n - 1] = nums[n - 1];
        for (int i= n - 2; i >= 0; i--) {
            right[i] = right[i + 1] * nums[i];
        }
        for (int i = 0; i < n; i++) {
            if (i == 0) result[i] = right[i + 1];
            else if (i == n - 1) result[i] = left[i - 1];
            else result[i] = left[i - 1] * right[i + 1];
        }
        return result;
    }
    public static int[] productExceptSelf2(int[] nums) {
        int count = 0;
        if (nums == null || nums.length == 0) return nums;
        int sum = 1;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == 0) {
                count++;
            } else {
                sum *= nums[i];
            }
        }
        int n = nums.length;
        int[] result = new int[n];
        if (count > 1) return result;
        for (int i = 0; i < n; i++) {
            if (nums[i] == 0) result[i] = sum;
            else if (count > 0) continue;
            else result[i] = sum / nums[i];
        }
        return result;
    }

    public static void main(String[] args) {
        int[] nums = {1,3,2,5,0};
        System.out.println(Arrays.toString(productExceptSelf(nums)));
        System.out.println(Arrays.toString(productExceptSelf2(nums)));
    }
}
