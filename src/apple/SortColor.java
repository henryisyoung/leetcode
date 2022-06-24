package apple;

import java.util.Arrays;

public class SortColor {
    public static void sortColor(int[] nums) {
        if (nums == null || nums.length < 2) return;
        int i = 0, j = nums.length - 1;
        while (i < j) {
            while (i < j && nums[i] == 0) {
                i++;
            }
            while (i < j && nums[j] == 1) {
                j--;
            }
            int tmp = nums[i];
            nums[i] = nums[j];
            nums[j] = tmp;
        }
        System.out.println(Arrays.toString(nums));
    }

    public static void sortColor2(int[] nums) {
        if (nums == null || nums.length < 2) return;
        int i = 0, j = nums.length - 1;
        int mid = i + (j - i) / 2;
        int k = mid, t = mid + 1;
        while ((i < k) && (t < j)) {
            while (i < k && nums[k] == 0) {
                k--;
            }
            while (i < k && nums[i] == 1) {
                i++;
            }
            swap(i, k ,nums);
            while (t < j && nums[t] == 0) {
                t++;
            }
            while (t < j && nums[j] == 1) {
                j--;
            }
            swap(j, t ,nums);
        }
        System.out.println(Arrays.toString(nums));
    }

    private static void swap(int i, int j, int[] nums) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }

    public static void main(String[] args) {
        int[] nums1 = {1,0,1,1,0,1};
        int[] nums2 = {1,0};
        int[] nums3 = {1,1,1};
        int[] nums4 = {1,1,1, 1,1, 0,0,0};
        sortColor(nums1);
        sortColor(nums2);
        sortColor(nums3);
        sortColor(nums4);
        System.out.println();
        sortColor2(nums1);
        sortColor2(nums2);
        sortColor2(nums3);
        sortColor2(nums4);;
    }
}
