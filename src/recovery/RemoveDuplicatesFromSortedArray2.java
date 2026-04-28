package recovery;

public class RemoveDuplicatesFromSortedArray2 {
    public int removeDuplicates(int[] nums) {
        if (nums == null || nums.length <= 2) {
            return nums.length;
        }
        int prev = 1, size= nums.length;
        int cur = 2;
        while (cur < size) {
            if (nums[cur] == nums[prev] && nums[cur] == nums[prev - 1]) {
                cur++;
            } else {
                prev++;
                nums[prev] = nums[cur];
                cur++;
            }
        }
        return prev + 1;
    }
}
