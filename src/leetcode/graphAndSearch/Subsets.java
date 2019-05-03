package leetcode.graphAndSearch;

import java.util.ArrayList;
import java.util.List;

public class Subsets {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        subsetsHelper(0, nums, result, new ArrayList<Integer>());
        return result;
    }

    private void subsetsHelper(int pos, int[] nums, List<List<Integer>> result, ArrayList<Integer> list) {
        result.add(new ArrayList<Integer>(list));

        for (int i = pos; i < nums.length; i++) {
            list.add(nums[i]);
            subsetsHelper(i + 1, nums, result, list);
            list.remove(list.size() - 1);
        }
    }

    private void subsets2Helper(int[] nums, List<List<Integer>> result, ArrayList<Integer> list, int pos) {
        result.add(new ArrayList<>(list));
        for (int i = pos; i < nums.length; i++) {
            list.add(nums[i]);
            subsets2Helper(nums, result, list, i + 1);
            list.remove(list.size() - 1);
        }
    }

    public static void main(String[] args) {
        int[] nums = {1,2,3};
        Subsets solver = new Subsets();
        List<List<Integer>> result = solver.subsets(nums);
        for (List<Integer> list : result) {
            System.out.println(list.toString());
        }
    }
}
