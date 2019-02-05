package leetcode.highFrequency;

import java.util.ArrayList;
import java.util.List;

public class MajorityElementII {
    public List<Integer> majorityElement(int[] nums) {
        List<Integer> rlt = new ArrayList<Integer>();
        if(nums == null || nums.length == 0){
            return rlt;
        }
        int n = nums.length, m1 = nums[0], c1 = 1, m2 = 0, c2 = 0;
        for(int i = 1; i < n; i++){
            int v = nums[i];
            if(m1 == v){
                c1++;
            }else if(m2 == v){
                c2++;
            }else if(c1 == 0){
                m1 = v;
                c1 = 1;
            }else if(c2 == 0){
                m2 = v;
                c2 = 1;
            }else{
                c1--; c2--;
            }
        }
        c1 = 0; c2 = 0;
        for(int i : nums){
            if(i == m1){
                c1++;
            }else if(i == m2){
                c2++;
            }
        }
        if(c1 > n/3){
            rlt.add(m1);
        }
        if(c2 > n/3){
            rlt.add(m2);
        }

        return rlt;
    }
    public List<Integer> majorityElement2(int[] nums) {
        List<Integer> result = new ArrayList<>();
        if (nums == null || nums.length == 0) {
            return result;
        }
        int can1 = 0, can2 = 0, count1 = 0, count2 = 0, n = nums.length;

        for (int i : nums) {
            if (count1 == 0) {
                can1 = i;
            } else if (count2 == 0){
                can2 = i;
            } else if (can1 == i) {
                count1++;
            } else if (can2 == i) {
                count2++;
            } else {
                count1--;
                count2--;
            }
        }
        count1 = 0;
        count2 = 0;
        for (int i : nums) {
            if (can1 == i) {
                count1++;
            }
            if (can2 == i) {
                count2++;
            }
        }
        if (count1 > n / 3) {
            result.add(can1);
        }
        if (count2 > n / 3) {
            result.add(can2);
        }
        return result;
    }
}
