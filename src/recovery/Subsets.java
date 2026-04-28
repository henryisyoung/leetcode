package recovery;

import java.util.ArrayList;
import java.util.List;

public class Subsets {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();

        dfsFindAllSubset(nums, result, new ArrayList<Integer>(), 0);
        return result;
    }

    private void dfsFindAllSubset(int[] nums, List<List<Integer>> result, ArrayList<Integer> list, int pos) {
        result.add(new ArrayList<>(list));
        for (int i = pos; i < nums.length; i++) {
            list.add(nums[i]);
            dfsFindAllSubset(nums, result, list, i + 1);
            list.removeLast();
        }
    }
}
