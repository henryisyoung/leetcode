package leetcode.highFrequency;

public class MajorityElement {
    public int majorityElement(int[] nums) {
        int candidate = 0, count = 0;
        for (int i : nums) {
            if (count == 0) {
                candidate = i;
            } else if (candidate == i) {
                count++;
            } else {
                count--;
            }
        }
        return candidate;
    }
    public int majorityElement2(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int can = nums[0];
        int count = 1;
        for (int i = 1; i < nums.length; i++) {
            if (can == nums[i]) {
                count++;
            } else {
                count--;
            }
            if (count == 0) {
                can = nums[i];
                count = 1;
            }
        }
        return can;
    }
}
