package recovery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Permutations2 {
    public List<List<Integer>> permuteUnique(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length == 0) {
            return result;
        }

        List<Integer> list = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        dfsFindAllPermutes(nums, list, result, visited);
        return result;
    }

    private void dfsFindAllPermutes(int[] nums, List<Integer> list, List<List<Integer>> result, Set<Integer> visited) {
        if (list.size() == nums.length) {
            result.add(new ArrayList<>(list));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            int num = nums[i];
            if (visited.contains(i) || (i != 0 && nums[i - 1] == nums[i] && !visited.contains(i - 1))) continue;
            list.add(num);
            visited.add(i);
            dfsFindAllPermutes(nums, list, result, visited);
            list.removeLast();
            visited.remove(i);
        }
    }
}
