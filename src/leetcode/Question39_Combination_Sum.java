package leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Question39_Combination_Sum {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        Arrays.sort(candidates);
        sumHelper(candidates, list, result, target, 0, 0);
        return result;
    }

    private void sumHelper(int[] nums, List<Integer> list, List<List<Integer>> result, int target, int pos, int sum) {
        if (sum == target){
            result.add(new ArrayList<Integer>(list));
            return;
        }
        for(int i = pos; i < nums.length; i++){
            int v = nums[i];
            if(sum + v > target) return;
            list.add(v);
            sumHelper(nums, list, result, target, i, sum + v);
            list.remove(list.size()-1);
        }
    }
}
