package datastructure.backtracking;

import java.util.ArrayList;
import java.util.List;

public class Permutations {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        findAll(nums, result, new ArrayList<>());
        return result;
    }

    private void findAll(int[] nums, List<List<Integer>> result, ArrayList<Integer> list) {
        if (list.size() == nums.length) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i : nums) {
            if (list.contains(i)) continue;
            list.add(i);
            findAll(nums, result, list);
            list.remove(list.size() - 1);
        }
    }
}
