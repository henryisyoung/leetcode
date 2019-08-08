package leetcode.graphAndSearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Permutations {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length == 0) return result;
        dfsSearchAll(new ArrayList<Integer>(), result, nums);
        return result;
    }

    private void dfsSearchAll(ArrayList<Integer> list, List<List<Integer>> result, int[] nums) {
        if (list.size() == nums.length) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if(list.contains(nums[i])) continue;
            list.add(nums[i]);
            dfsSearchAll(list, result, nums);
            list.remove(list.size() - 1);
        }
    }

    public List<List<Integer>> permute2(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length == 0) return result;
        int n = nums.length;
        boolean[] visited = new boolean[n];
        Arrays.sort(nums);
        dfsSearchAll2(nums, result, new ArrayList<>(), visited);
        return result;
    }

    private void dfsSearchAll2(int[] nums, List<List<Integer>> result, List<Integer> list, boolean[] visited) {
        if (list.size() == nums.length) {
//            System.out.println(list.toString());
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (visited[i] || (i != 0 && nums[i - 1] == nums[i] && visited[i - 1])) {
                continue;
            }
//            System.out.println("i " + i + " nums[i] " + nums[i]);
            visited[i] = true;
            list.add(nums[i]);
            dfsSearchAll2(nums, result, list, visited);
//            System.out.println(list.toString());
            visited[i] = false;
            list.remove(list.size() - 1);
        }
    }

    public static void main(String[] args) {
        Permutations solver = new Permutations();
        solver.permute2(new int[]{1,1,2});
    }
}
