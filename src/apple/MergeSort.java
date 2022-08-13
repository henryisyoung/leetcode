package apple;

import java.util.Arrays;

public class MergeSort {
    public static void mergeSort(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }
        mergeSortHelper(nums, 0 , nums.length - 1);
    }

    private static void mergeSortHelper(int[] nums, int l, int r) {
        if (l >= r) return;
        int mid = l + (r - l) / 2;
        mergeSortHelper(nums, l, mid);
        mergeSortHelper(nums, mid + 1, r);
        merger(nums, l , mid, r);
    }

    private static void merger(int[] nums, int l, int m, int r){
        int[] copy = new int[r - l + 1];
        int index = 0;
        int i = l, j = m + 1;
        while (i <= m && j <= r) {
            copy[index++] = nums[i] < nums[j] ? nums[i++] : nums[j++];
        }
        while (i <= m) {
            copy[index++] = nums[i++];
        }
        while (j <= r) {
            copy[index++] = nums[j++];
        }
        for (i = 0; i < copy.length; i++) {
            nums[i + l] = copy[i];
        }
    }

    public static void quickSort(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }
        quickSortHelper(nums, 0, nums.length - 1);
    }

    private static void quickSortHelper(int[] nums, int l, int r) {
        if (l >= r) return;
        int pos = partition(nums, l, r);
        quickSortHelper(nums, l, pos);
        quickSortHelper(nums, pos + 1, r);
    }

    private static int partition(int[] nums, int l, int r) {
        int pivot = nums[l];
        while (l < r) {
            while (l < r && nums[r] >= pivot) {
                r--;
            }
            nums[l] = nums[r];
            while (l < r && nums[l] <= pivot) {
                l++;
            }
            nums[r] = nums[l];
        }
        nums[l] = pivot;
        return l;
    }

    public static int[] deDup(int[] nums) {
        quickSort(nums);
        System.out.println("sorted " + Arrays.toString(nums));
        int pos = dedupHelper(nums);
        int j = nums.length - 1;
        while (pos >= 0) {
            nums[j--] = nums[pos];
            nums[pos--] = 0;
        }
        return nums;
    }

    private static int dedupHelper(int[] nums) {
        int i = 0, j = 0, n = nums.length;
        while (j < n) {
            while (j < n && nums[j] == nums[i]) {
                j++;
            }
            if (j < n) {
                System.out.println(Arrays.toString(nums));
                i++;
                nums[i] = nums[j];
            }
        }
        return i;
    }

    public static void main(String[] args) {
//        int[] nums = {3,6,1,4,5,1,3};
//        mergeSort(nums);
//        System.out.println(Arrays.toString(nums));

//        int[] nums2 = {1,4,7,4,3,2,0,1,1,1};
//        quickSort(nums2);
//        System.out.println(Arrays.toString(nums2));

        int[] nums = {4,3,5,6,4,7,4,4,4,3,0,0};
        System.out.println(Arrays.toString(deDup(nums)));
    }
}
