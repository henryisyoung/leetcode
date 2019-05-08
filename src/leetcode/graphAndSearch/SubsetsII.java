package leetcode.graphAndSearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubsetsII {
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length == 0) {
            return result;
        }
        Arrays.sort(nums);
        dfsSearchAll(0, new ArrayList<Integer>(), result, nums);
        return result;
    }

    private void dfsSearchAll(int pos, ArrayList<Integer> list, List<List<Integer>> result, int[] nums) {
        result.add(new ArrayList<>(list));
        for (int i = pos; i < nums.length; i++) {
            if (i != pos && nums[i] == nums[i - 1]) {
//                System.out.println(list.toString() + " test i " + i + " pos " + pos);
                continue;
            }
            System.out.println(list.toString() + " i " + i + " pos " + pos);
            System.out.println(" nums[i] " + nums[i]);
            list.add(nums[i]);
            dfsSearchAll(i + 1, list, result, nums);
            list.remove(list.size() - 1);
        }

    }

    public static void main(String[] args) {
        int[] nums = {1,2,3, 2};
        SubsetsII solver = new SubsetsII();
        List<List<Integer>> result = solver.subsetsWithDup(nums);
        for (List<Integer> list : result) {
            System.out.println(list.toString());
        }
    }
}
