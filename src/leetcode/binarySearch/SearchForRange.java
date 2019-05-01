package leetcode.binarySearch;

import java.util.Arrays;

public class SearchForRange {
    public int[] searchRange(int[] A, int target) {
        // write your code here
        if (A == null || A.length == 0) {
            return new int[]{-1,-1};
        }
        int[] result = new int[2];
        result[0] = findFirst(A, target);
        result[1] = findLast(A, target);
        return result;
    }

    private int findLast(int[] a, int target) {
        int start = 0, end = a.length - 1;
        while (start + 1 < end) {
            int mid = start + (end - start)/2;
            if (a[mid] <= target) {
                start = mid;
            } else {
                end = mid;
            }
        }
        if (a[end] == target) {
            return end;
        }
        if (a[start] == target) {
            return start;
        }
        return -1;
    }

    private int findFirst(int[] a, int target) {
        int start = 0, end = a.length - 1;
        while (start + 1 < end) {
            int mid = start + (end - start)/2;
            if (a[mid] >= target) {
                end = mid;
            } else {
                start = mid;
            }
        }
        if (a[start] == target) {
            return start;
        }
        if (a[end] == target) {
            return end;
        }
        return -1;
    }

    public static void main(String[] args) {
        SearchForRange sovler = new SearchForRange();
        int[] A = new int[1], A2 = {5, 7, 7, 8, 8, 10};
        int[] result1 = sovler.searchRange(A, 9);
        int[] result2 = sovler.searchRange(A2, 8);
        System.out.println(Arrays.toString(result1));
        System.out.println(Arrays.toString(result2));
    }
}
