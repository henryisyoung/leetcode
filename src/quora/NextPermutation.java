package quora;

import java.util.Arrays;

public class NextPermutation {
    //http://fisherlei.blogspot.com/2012/12/leetcode-next-permutation.html
    public void nextPermutation(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }
        int n = nums.length;
        int pos = n - 1;
        while (pos > 0 && nums[pos] <= nums[pos - 1]) {
            pos--;
        }
        if(pos == 0) {
            reverse(nums, 0, n - 1);
            return;
        }
        int index = pos - 1, val = nums[index];
//        System.out.println("index " + index + " val " + val);
        for (int i = n - 1; i > index; i--) {
            if (nums[i] > val) {
                swap(nums, i , index);
                break;
            }
        }
//        System.out.println(Arrays.toString(nums));

        reverse(nums, index + 1, n - 1);
//        System.out.println(Arrays.toString(nums));
    }

    private void reverse(int[] nums, int s, int e) {
        while (s < e) {
            swap(nums, s, e);
            s++;
            e--;
        }
    }

    private void swap(int[] nums, int s, int e) {
        int temp = nums[s];
        nums[s] = nums[e];
        nums[e] = temp;
    }

    public static void main(String[] args) {
        int[] nums = {6,8,7,4,3,2};
        NextPermutation solver = new NextPermutation();
        solver.nextPermutation(nums);
    }
}
