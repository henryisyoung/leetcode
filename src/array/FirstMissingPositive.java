package array;

public class FirstMissingPositive {
    public static int firstMissingPositive(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 1;
        }
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            while (nums[i] > 0 && nums[i] <= n && nums[i] != i + 1 && nums[i] != nums[nums[i] - 1]) {
                System.out.println(nums[i]);
                int tem = nums[nums[i] - 1];
                nums[nums[i] - 1] = nums[i];
                nums[i] = tem;
            }
        }
        int i = 0;
        for (i = 0; i < n; i++) {
            if (nums[i] != (i + 1)) {
                return i + 1;
            }
        }
        return i + 1;
    }

    public static void main(String[] args) {
        int[] nums = {3,4,-1,1};
        firstMissingPositive(nums);
    }
}
