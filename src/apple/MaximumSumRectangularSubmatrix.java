package apple;

import java.util.*;

public class MaximumSumRectangularSubmatrix {
    public static int findMax(int[][] board) {
        int rows = board.length, cols = board[0].length;
        int max = Integer.MIN_VALUE;
        int[] array = new int[rows];

        for (int start = 0; start < cols; start++) {
            array = new int[rows];
            for (int end = start; end < cols; end++) {
                for (int r = 0; r < rows; r++) {
                    array[r] += board[r][end];
                }
                max = Math.max(findOneDMax(array), max);
            }
        }
        return max;
    }

    private static int findOneDMax(int[] array) {
        int prev = array[0];
        int max = prev;
        for (int i = 1; i < array.length; i++) {
            if (prev < 0) {
                max = Math.max(max, array[i]);
                prev = array[i];
            } else {
                max = Math.max(max, prev + array[i]);
                prev += array[i];
            }
        }
        return max;
    }

    public static void main(String[] args) {
        int[] nums = {-1,3,-2,5,6};
        System.out.println(findOneDMax(nums));

        int[][] board = {
                {2, 1,-3,-4, 5},
                {0, 6, 3, 4, 1},
                {2,-2,-1, 4,-5},
                {-3,3, 1, 0, 3},
        };

        System.out.println(findMax(board));

        int[][] dirs = {{1,1},{0,0}};
        Set<List<String>> set = new HashSet<>();
        List<String> list = new ArrayList<>();
        for (int[] dir : dirs) {
            list.add(Arrays.toString(dir));
        }
        int[][] dirs2 = {{1,1},{0,0}};
        List<String> list2 = new ArrayList<>();
        for (int[] dir2 : dirs2) {
            list2.add(Arrays.toString(dir2));
        }
        set.add(list);
        set.add(list2);
        System.out.println(set);
    }
}
