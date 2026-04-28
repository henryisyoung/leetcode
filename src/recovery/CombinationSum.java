package recovery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombinationSum {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        if (candidates == null || candidates.length == 0) {
            return new ArrayList<>();
        }
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);

        dfsFindAll(candidates, result, new ArrayList<>(), 0, 0, target);

        return result;
    }

    private void dfsFindAll(int[] candidates, List<List<Integer>> result, ArrayList<Integer> list, int pos, int sum, int target) {
        if (sum == target) {
            result.add(new ArrayList<>(list));
            return;
        }

        for (int i = pos; i < candidates.length; i++) {
            if (sum + candidates[i] > target) return;
            list.add(candidates[i]);
            dfsFindAll(candidates, result, list, i, sum + candidates[i], target);
            list.removeLast();
        }
    }
}
