package reddit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombinationSum {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        dfsFindAll(target, 0, result, candidates, new ArrayList<Integer>(), 0);
        return result;
    }

    private void dfsFindAll(int target, int cur, List<List<Integer>> result,
                            int[] candidates, List<Integer> list, int pos) {
        if (cur == target) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = pos; i < candidates.length; i++) {
            if (cur + candidates[i] > target) {
                break;
            }
            list.add(candidates[i]);
            dfsFindAll(target, cur + candidates[i], result, candidates, list, i);
            list.remove(list.size() - 1);
        }
    }

    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        dfsFindAll2(target, 0, result, candidates, new ArrayList<Integer>(), 0);
        return result;
    }

    private void dfsFindAll2(int target, int cur, List<List<Integer>> result,
                            int[] candidates, List<Integer> list, int pos) {
        if (cur == target) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = pos; i < candidates.length; i++) {
            if (cur + candidates[i] > target) {
                break;
            }
            list.add(candidates[i]);
            dfsFindAll(target, cur + candidates[i], result, candidates, list, i);
            list.remove(list.size() - 1);
        }
    }
}
