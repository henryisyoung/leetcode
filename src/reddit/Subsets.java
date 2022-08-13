package reddit;

import java.util.ArrayList;
import java.util.List;

public class Subsets {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length == 0) {
            return result;
        }
        dfsFindAll(nums, result, new ArrayList<>(), 0);
        return result;
    }

    private void dfsFindAll(int[] nums, List<List<Integer>> result, ArrayList<Integer> list, int pos) {
        result.add(new ArrayList<>(list));
        for (int i = pos; i < nums.length; i++) {
            list.add(nums[i]);
            dfsFindAll(nums, result, list, i + 1);
            list.remove(list.size() - 1);
        }
    }
}
