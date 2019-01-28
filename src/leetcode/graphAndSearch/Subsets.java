package leetcode.graphAndSearch;

import java.util.ArrayList;
import java.util.List;

public class Subsets {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        subsetsHelper(0, nums, result, new ArrayList<Integer>());
        return result;
    }

    private void subsetsHelper(int pos, int[] nums, List<List<Integer>> result, ArrayList<Integer> list) {
        result.add(new ArrayList<Integer>(list));

        for (int i = pos; i < nums.length; i++) {
            list.add(nums[i]);
            subsetsHelper(i + 1, nums, result, list);
            list.remove(list.size() - 1);
        }
    }
}