package quora;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Subset {
    public List<Integer> allMultiply(int[] nums) {
        List<Integer> result = new ArrayList<>();
        if (nums == null || nums.length == 0) {
            return result;
        }
        Set<Integer> set = new HashSet<>();
        dfsFindAll(set, Integer.MAX_VALUE, nums, 0);
        result.addAll(set);
        return result;
    }

    private void dfsFindAll(Set<Integer> set, int cur, int[] nums, int pos) {
        if (cur != Integer.MAX_VALUE) {
            set.add(cur);
        }
        for (int i = pos; i < nums.length; i++) {
            if (cur == Integer.MAX_VALUE) {
                dfsFindAll(set, nums[i], nums, i + 1);
            } else {
                dfsFindAll(set, nums[i] * cur, nums, i + 1);
            }
        }
    }

    public static void main(String[] args) {
        int[] nums = {1, 2, 3};
        Subset solver = new Subset();
        List<Integer> result = solver.allMultiply(nums);
        System.out.println(result.toString());
    }
}
