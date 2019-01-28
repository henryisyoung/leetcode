package leetcode.graphAndSearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombinationSum {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        calHelper(candidates, target, 0, result, new ArrayList<Integer>(), 0);
        return result;
    }

    private void calHelper(int[] candidates, int target, int pos, List<List<Integer>> result, ArrayList<Integer> list, int sum) {
        if (sum == target) {
            result.add(new ArrayList<Integer>(list));
            return;
        }
        for (int i = pos; i < candidates.length; i++) {
            if (sum + candidates[i] > target) {
                break;
            }
            list.add(candidates[i]);
            calHelper(candidates, target, i, result, list, sum + candidates[i]);
            list.remove(list.size() - 1);
        }
    }
}
