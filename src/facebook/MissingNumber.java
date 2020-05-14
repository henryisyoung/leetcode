package facebook;

import java.util.Arrays;

public class MissingNumber {
    public static int missingNumber(int[] nums) {
        Arrays.sort(nums);
        int left = 0, right = nums.length;
        while(left<right){
            int mid = (left + right)/2;
            if(nums[mid]>mid) right = mid;
            else left = mid+1;
        }
        return left;
    }

    public static int missingNumber2(int[] nums) {
        Arrays.sort(nums);
        int base = nums[0];
        int left = 0, right = nums.length - 1;
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] - base > mid) {
                right = mid;
            } else {
                left = mid;
            }
        }
        if (nums[left] - base > left) return base + left;
        if (nums[right] - base > right) return base + right;
        return nums[right] + 1;
    }
    public static void main(String[] args) {
        int[] nums = {6,3,4};
//        System.out.println(missingNumber(nums));
        System.out.println(missingNumber2(nums));
    }
}
