package recovery;

public class KthLargestElementArray {
    public static int findKthLargest(int[] nums, int k) {
        return quickSelectFinder(0, nums.length - 1, k - 1, nums);
    }

    private static int quickSelectFinder(int l, int r, int k, int[] nums) {
        if (l >= r) return nums[l];
        int partition = findPartition(l, r, nums);
        if (partition == k) {
            return nums[partition];
        }
        else if (partition > k) {
            return quickSelectFinder(l, r - 1, k , nums);
        }

        return quickSelectFinder(l = 1, r, k , nums);
    }

    private static int findPartition(int l, int r, int[] nums) {
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

    public static void main(String[] args) {
        int[] nums = {1,5,3,4,9};
        System.out.println(findKthLargest(nums, 3));
    }

}