package recovery;

import java.util.ArrayList;
import java.util.List;

public class Permutations {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length == 0) {
            return result;
        }

        List<Integer> list = new ArrayList<>();
        dfsFindAllPermutes(nums, list, result);
        return result;
    }

    private void dfsFindAllPermutes(int[] nums, List<Integer> list, List<List<Integer>> result) {
        if (list.size() == nums.length) {
            result.add(new ArrayList<>(list));
            return;
        }

        for (int num : nums) {
            if (list.contains(num)) continue;
            list.add(num);
            dfsFindAllPermutes(nums, list, result);
            list.removeLast();
        }
    }
}
