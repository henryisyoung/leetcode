package leetcode.graphAndSearch;

import java.util.ArrayList;
import java.util.List;

public class Permutations {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        permuteHelper(nums, result, new ArrayList<Integer>());
        return result;
    }

    private void permuteHelper(int[] nums, List<List<Integer>> result, List<Integer> list) {
        if(nums.length == list.size()) {
            result.add(new ArrayList<Integer>(list));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (! list.contains(nums[i])){
                list.add(nums[i]);
                permuteHelper( nums, result, list);
                list.remove(list.size() - 1);
            }
        }
    }
}
