package leetcode.highFrequency;

import java.util.*;

public class ContinuousSubarraySumLintCode {
    public List<Integer> continuousSubarraySum(int[] A) {
        List<Integer> result = new ArrayList<>();
        if (A == null || A.length == 0) {
            return result;
        }
        int[] arr = new int[2];
        int sum = 0, left = 0, end = 0, max = Integer.MIN_VALUE, n = A.length;
        for (int i = 0; i < n; i++) {
            if (sum < 0) {
                sum = A[i];
                left = i;
                end = i;
            } else {
                sum += A[i];
                end = i;
            }
            if (sum > max) {
                max = sum;
                arr[0] = left;
                arr[1] = end;
            }
        }
        result.add(arr[0]);
        result.add(arr[1]);
        return result;
    }
}
