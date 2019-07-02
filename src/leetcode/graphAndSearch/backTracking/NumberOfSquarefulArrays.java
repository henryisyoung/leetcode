package leetcode.graphAndSearch.backTracking;

import java.util.*;

public class NumberOfSquarefulArrays {
    int count = 0;
    public int numSquarefulPerms(int[] A) {
        if (A == null || A.length == 0) {
            return 0;
        }
        Arrays.sort(A);
        boolean[] isVisited = new boolean[A.length];
        dfsFindAll(A, isVisited,  new ArrayList<>());
        return count;
    }

    private void dfsFindAll(int[] nums, boolean[] isVisited, List<Integer> list) {
        if (list.size() == nums.length) {
            System.out.println(list.toString());
            count++;
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (isVisited[i] || (i != 0  && !isVisited[i - 1]) && nums[i-1] == nums[i]) {
                continue;
            }
            if (list.size() != 0 && !squareful(list.get(list.size() - 1), nums[i])) {
                continue;
            }
            isVisited[i] = true;
            list.add(nums[i]);
            dfsFindAll(nums, isVisited, list);
            list.remove(list.size() - 1);
            isVisited[i] = false;
        }
    }
    private boolean squareful(int x, int y) {
        int s = (int) Math.sqrt(x + y);

        return s * s == x + y;
    }

    public static void main(String[] args) {
        int[] A = {2,2,2};
        int[] A2 = {65,44,5,11};
        int[] A3 = {1,1,8,1,8};
        NumberOfSquarefulArrays solver = new NumberOfSquarefulArrays();
//        System.out.println(solver.numSquarefulPerms(A));
        System.out.println(solver.numSquarefulPerms(A3));
    }
}
