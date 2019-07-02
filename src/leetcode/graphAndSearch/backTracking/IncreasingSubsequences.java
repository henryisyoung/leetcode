package leetcode.graphAndSearch.backTracking;

import java.util.ArrayList;
import java.util.*;

public class IncreasingSubsequences {
    public List<List<Integer>> findSubsequences(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length == 0) {
            return result;
        }
        dfsSearchAll(nums, new ArrayList<Integer>(), result, 0);
        return result;
    }

    private void dfsSearchAll(int[] nums, ArrayList<Integer> list, List<List<Integer>> result, int pos) {
        if (list.size() > 1) result.add(new ArrayList<>(list));
        Set<Integer> set = new HashSet<>();
        for (int i = pos; i < nums.length; ++i) {
            if (!set.add(nums[i])) continue;
            if (list.isEmpty() || nums[i] >= list.get(list.size()-1)) {
                list.add(nums[i]);
                dfsSearchAll(nums, list, result, i+1);
                list.remove(list.size()-1);
            }
        }
    }

    public static void main(String[] args) {
        int[] nums = {4, 6, 7, 7};
        IncreasingSubsequences solver = new IncreasingSubsequences();
        List<List<Integer>> result = solver.findSubsequences(nums);
        for (List<Integer> list : result) {
            System.out.println(list.toString());
        }
    }
}
