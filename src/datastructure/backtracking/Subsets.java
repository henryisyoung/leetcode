package datastructure.backtracking;

import java.util.*;

public class Subsets {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();

        dfsFindAll(nums, 0, result, new ArrayList<Integer>());
        return result;
    }

    private void dfsFindAll(int[] nums, int pos, List<List<Integer>> result, ArrayList<Integer> list) {
        result.add(new ArrayList<>(list));

        for (int i = pos; i < nums.length; i++) {
            list.add(nums[i]);
            dfsFindAll(nums, i + 1, result, list);
            list.remove(list.size() - 1);
        }
    }
}
