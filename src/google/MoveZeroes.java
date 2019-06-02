package google;

import java.util.Arrays;

public class MoveZeroes {
    //跟利口move zeros差不多，只不过要把0挪到左边，然后写testcase，问如果在production环境，碰到invalid inpu要怎么处理。
    public void moveZeroes(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }
        int index = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                nums[index] = nums[i];
                index++;
            }
        }
        while (index < nums.length) {
            nums[index++] = 0;
        }
    }
    public void moveZeroes2(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }
        int index = nums.length - 1;
        for (int i = nums.length - 1; i >= 0; i--) {
            if (nums[i] != 0) {
                nums[index--] = nums[i];
            }
        }
        while (index >= 0) {
            nums[index--] = 0;
        }
        System.out.println(Arrays.toString(nums));
    }

    public static void main(String[] args) {
        int[] nums = {0,1,0,3,12};
        MoveZeroes solver = new MoveZeroes();
        solver.moveZeroes2(nums);
    }
}
