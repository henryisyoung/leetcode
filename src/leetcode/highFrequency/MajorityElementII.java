package leetcode.highFrequency;

import java.util.ArrayList;
import java.util.List;

public class MajorityElementII {
    public List<Integer> majorityElement(int[] nums) {
        List<Integer> result = new ArrayList<>();
        if (nums == null || nums.length == 0) return result;
        int cand1 = nums[0], count1 = 1, cand2 = 0, count2 = 0;
        for (int i = 1; i < nums.length; i++) {
            int cur = nums[i];
            if (cand1 == cur) {
                count1++;
            } else if (cand2 == cur) {
                count2++;
            } else if (count1 == 0) {
                cand1 = cur;
                count1 = 1;
            } else if (count2 == 0) {
                cand2 = cur;
                count2 = 1;
            } else {
                count1--;
                count2--;
            }
        }
        count1 = 0;
        count2 = 0;
        for (int i : nums) {
            if (i == cand1) count1++;
            else if (i == cand2) count2++;
        }
        if (count1 > nums.length / 3) result.add(cand1);
        if (count2 > nums.length / 3) result.add(cand2);
        return result;
    }
}
