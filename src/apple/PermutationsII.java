package apple;

import java.util.*;

public class PermutationsII {
    public static List<List<Integer>> permuteUnique(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length == 0) return new ArrayList<>();
        Arrays.sort(nums);
        dfsFindAll(nums, new ArrayList<>(), new HashSet<>(), result);
        return (result);
    }

    private static void dfsFindAll(int[] nums, ArrayList<Integer> list, HashSet<Integer> visited, List<List<Integer>> result) {
        if (list.size() == nums.length) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (visited.contains(i) || (i != 0 && nums[i - 1] == nums[i] && !visited.contains(i - 1))) continue;
            visited.add(i);
            list.add(nums[i]);
            dfsFindAll(nums, list, visited, result);
            list.remove(list.size() - 1);
            visited.remove(i);
        }
    }

    public static void main(String[] args) {
        int[] nums = {1,1,2};
        System.out.println(permuteUnique(nums));
    }
}
