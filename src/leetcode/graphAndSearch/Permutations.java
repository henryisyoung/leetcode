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

    public List<List<Integer>> permute2(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        permute2Helper(result, nums, new ArrayList<>());
        return result;
    }

    private void permute2Helper(List<List<Integer>> result, int[] nums, ArrayList<Integer> list) {
        if (list.size() == nums.length) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if(list.contains(nums[i])) {
                continue;
            }
            list.add(nums[i]);
            permute2Helper(result, nums, list);
            list.remove(list.size() - 1);
        }
    }
}
