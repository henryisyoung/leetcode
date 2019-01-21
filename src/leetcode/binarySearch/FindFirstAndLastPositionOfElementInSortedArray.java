package leetcode.binarySearch;

public class FindFirstAndLastPositionOfElementInSortedArray {
    public int[] searchRange(int[] nums, int target) {
        int first = -1, second = -1;
        int[] result = new int[2];
        result[0] = first;
        result[1] = second;
        if(nums == null || nums.length == 0 || nums[0] > target){
            return result;
        }
        int start = 0, end = nums.length - 1;
        while (start + 1 < end){
            int mid = start + (end - start)/2;
            if(nums[mid] >= target){
                end = mid;
            }else {
                start = mid;
            }
        }

        if (nums[start] == target){
            first = start;
        } else if(nums[end] == target){
            first = end;
        }
        result[0] = first;

        start = 0;
        end = nums.length - 1;
        while (start + 1 < end){
            int mid = start + (end - start)/2;
            if(nums[mid] <= target){
                start = mid;
            }else {
                end = mid;
            }
        }
        if (nums[end] == target){
            second = end;
        } else if(nums[start] == target){
            second = start;
        }
        result[1] = second;
        return result;
    }
}
