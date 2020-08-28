package contest;

public class MinimumNumbersFunctionCalls {
    public static int minOperations(int[] nums) {
        int count = 0;
        while(!valid(nums)) {
            int odd = 0;
            for (int i = 0; i < nums.length; i++) {
                if (nums[i] % 2 == 1) {
                    odd++;
                    nums[i]--;
                }
            }
            if (odd != 0) {
                count += odd;
            } else {
                for (int i = 0; i < nums.length; i++) {
                    nums[i] /= 2;
                }
                count++;
            }
        }
        return count;
    }

    private static boolean valid(int[] nums) {
        int count  = 0;
        for (int i : nums) {
            if (i == 0) count++;
        }
        return count == nums.length;
    }

    public static void main(String[] args) {
        int[] nums = {2,4,8,16};
        System.out.println(minOperations(nums));
    }
}
