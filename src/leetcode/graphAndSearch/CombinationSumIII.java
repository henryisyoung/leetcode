package leetcode.graphAndSearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombinationSumIII {
    public static List<List<Integer>> combinationSum3(int k, int n) {
        List<List<Integer>> result = new ArrayList<>();
        dfsSearchAll(1, k, n, 0, new ArrayList<>(), result);
        return result;
    }

    private static void dfsSearchAll(int pos, int k, int target, int cur, List<Integer> list, List<List<Integer>> result) {
        if (list.size() == k && cur == target) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = pos; i <= 9; i++) {
            if (cur + i > target) break;
            list.add(i);
            dfsSearchAll(i + 1, k , target, cur + i, list, result);
            list.remove(list.size() - 1);
        }
    }

    public static void main(String[] args) {
        int k = 3, n = 9;
        List<List<Integer>> result = combinationSum3(k, n);
        System.out.println(result.toString());
    }
}
