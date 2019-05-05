package leetcode.highFrequency;

import java.util.ArrayList;
import java.util.List;

public class ContinuousSubarraySumLintCode2 {
    public List<Integer> continuousSubarraySum(int[] A) {
        List<Integer> result = new ArrayList<>();
        if (A == null || A.length == 0) {
            return result;
        }
        int[] arr = new int[2];

        int sum = 0, left = 0, end = 0, max = Integer.MIN_VALUE, n = A.length;
        int[] newA = new int[2 * n];
        for (int i = 0; i < n; i++) {
            newA[i] = A[i];
            newA[i + n] = A[i];
        }
        for (int i = 0; i < n * 2; i++) {
            if (sum < 0) {
                sum = newA[i];
                left = i;
                end = i;
            } else {
                sum += newA[i];
                end = i;
            }
            if (sum > max) {
                max = sum;
                arr[0] = newA[left];
                arr[1] = newA[end];
            }
        }
        result.add(arr[0]);
        result.add(arr[1]);
        return result;
    }
}
