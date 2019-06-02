package leetcode.twoPointer;

import java.util.*;

public class SummaryRanges {
    public List<String> summaryRanges(int[] nums) {
        List<String> result = new ArrayList<>();
        if (nums == null || nums.length == 0) {
            return result;
        }
        int i = 0, j = 1, n = nums.length;
        while (i < n) {
            while (j < n && nums[j] == nums[j - 1] + 1) {
                j++;
            }
            result.add(createRange(i, j - 1, nums));
            i = j;
            j++;
        }
        return result;
    }

    private String createRange(int i, int j, int[] nums) {
        if (i == j) {
            return Integer.toString(nums[i]);
        }
        return nums[i] + "->" + nums[j];
    }

    public static void main(String[] args) {
        SummaryRanges sovler = new SummaryRanges();
        List<String> result = sovler.summaryRanges(new int[]{0,2,3,4,6,8,9});
        System.out.println(result.toString());
    }
}
