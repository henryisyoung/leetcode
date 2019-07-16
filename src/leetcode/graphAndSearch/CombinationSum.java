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

    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        sumHelper(candidates, target, 0, 0, new ArrayList<Integer>(), result);
        return result;
    }

    private void sumHelper(int[] candidates, int target, int sum, int pos, ArrayList<Integer> list, List<List<Integer>> result) {
        if (sum == target) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = pos; i < candidates.length; i++) {
            if (sum + candidates[i] > target) {
                break;
            }
            list.add(candidates[i]);
            sumHelper(candidates, target, sum + candidates[i], i, list, result);
            list.remove(list.size() - 1);
        }
    }


    public List<List<Integer>> combinationSumDP(int[] candidates, int target) {
        List<List<List<Integer>>> dp = new ArrayList<>();//be careful, 0 base index
        Arrays.sort(candidates);

        for(int t=1; t<=target; t++) {
            ArrayList<List<Integer>> newList = new ArrayList<>();
            for(int i=0; i<candidates.length && candidates[i]<=t; i++) {
                if(t==candidates[i]) {
                    newList.add(Arrays.asList(candidates[i]));
                } else {
                    for(List<Integer> list: dp.get(t-candidates[i]-1)) {
                        //this is to pick the list seq which is monotonic increasing
                        //this can avoid duplicates
                        if(candidates[i]>=list.get(0)) {
                            List<Integer> cumList = new ArrayList<>();
                            cumList.add(candidates[i]); cumList.addAll(list);
                            newList.add(cumList);
                        }
                    }
                }
            }
            dp.add(newList);
        }

        return dp.get(target-1);
    }
}
