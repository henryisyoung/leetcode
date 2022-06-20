package datastructure.backtracking;

import java.util.*;

public class CombinationSum {
    public static List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);

        dfsSearch(candidates, result, new ArrayList<>(), 0, target, 0);
        return result;
    }

    private static void dfsSearch(int[] candidates, List<List<Integer>> result, ArrayList<Integer> list, int sum, int target, int pos) {
        if (sum == target) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = pos; i < candidates.length; i++) {
            if (sum + candidates[i] > target) break;
            list.add(candidates[i]);
            dfsSearch(candidates, result, list, sum + candidates[i], target, i);
            list.remove(list.size() - 1);
        }
    }

    public static void main(String[] args) {
        int[] candidates = {2,3,6,7};
        int target = 7;
        System.out.println(combinationSum(candidates, target));
    }
}
