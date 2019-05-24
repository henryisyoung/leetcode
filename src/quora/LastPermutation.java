package quora;

import java.util.ArrayList;
import java.util.Arrays;

public class LastPermutation {
    //http://fisherlei.blogspot.com/2012/12/leetcode-next-permutation.html
    public ArrayList<Integer> previousPermuation(ArrayList<Integer> nums) {
        // write your code
        int i = nums.size() - 2;
        while (i >= 0 && nums.get(i + 1) >= nums.get(i)) {
            i--;
        }
        if (i >= 0 ) {
            int j = nums.size() - 1;
            while (j >= 0 && nums.get(j) >= nums.get(i)) {
                j--;
            }
            swap(nums, i, j);
        }
        reverse(nums, i + 1, nums.size() - 1);
        return nums;
    }

    public void swap(ArrayList<Integer> nums, int i, int j) {
        int tmp = nums.get(i);
        nums.set(i, nums.get(j));
        nums.set(j, tmp);
    }

    public void reverse(ArrayList<Integer> nums, int i, int j) {
        while (i < j) {
            swap(nums, i++, j--);
        }
    }

    public static void main(String[] args) {
        int[] nums = {6,8,7,4,3,2};
        LastPermutation solver = new LastPermutation();
    }
}
