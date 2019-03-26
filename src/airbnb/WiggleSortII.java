package airbnb;

import java.util.Arrays;

public class WiggleSortII {
    public void wiggleSortVirtualIndex(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }
        int n = nums.length;
        int median = kthLargestElement(n / 2 + 1, nums);

        int left = 0, right = n - 1;
        int index = 0;
        while (index <= right) {
            if (nums[newIndex(index, n)] > median) {
                swap(nums, newIndex(left++, n), newIndex(index++, n));
            } else if (nums[newIndex(index, n)] < median) {
                swap(nums, newIndex(right--, n), newIndex(index, n));
            } else {
                index++;
            }
        }
        System.out.println(Arrays.toString(nums));
    }

    private void swap(int[] nums, int i, int j) {
        int tem = nums[i];
        nums[i] = nums[j];
        nums[j] = tem;
    }

    public int newIndex(int index, int n) {
        return (1 + 2 * index) % (n | 1);
    }

    public int kthLargestElement(int k, int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        if (k <= 0) {
            return 0;
        }
        return helper(nums, 0, nums.length - 1, nums.length - k);
    }

    public int helper(int[] nums, int l, int r, int k) {
        if (l == r) {
            return nums[l];
        }
        int position = partition(nums, l, r);

        if (position == k) {
            return nums[position];
        } else if (position < k) {
            return helper(nums, position + 1, r, k);
        }  else {
            return helper(nums, l, position - 1, k);
        }
    }
    public int partition(int[] nums, int l, int r) {
        // 初始化左右指针和pivot
        int left = l, right = r;
        int pivot = nums[left];

        // 进行partition
        while (left < right) {
            while (left < right && nums[right] >= pivot) {
                right--;
            }
            nums[left] = nums[right];
            while (left < right && nums[left] <= pivot) {
                left++;
            }
            nums[right] = nums[left];
        }

        // 返还pivot点到数组里面
        nums[left] = pivot;
        return left;
    }

    public static void wiggleSort(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }
        int n = nums.length;
        Arrays.sort(nums);
        int i = (n - 1) / 2, j = n - 1;
        int[] table = new int[n];
        for (int k = 0; k < n; k++) {
            if (k % 2 == 0) {
                table[k] = nums[i--];
            } else {
                table[k] = nums[j--];
            }
        }
        for (int k = 0; k < n; k++) {
            nums[k] = table[k];
        }
    }

    public static void main(String[] args) {
        int[] arr = {3,5,2,1,6,4};
        WiggleSortII solver = new WiggleSortII();
        solver.wiggleSortVirtualIndex(arr);
    }
}
