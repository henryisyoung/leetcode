package contest;

import leetcode.dataStructrue.HashHeap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GetMaximumScore {
    public int maxSum(int[] nums1, int[] nums2) {
        long MOD = (long)1e9+7;
        int m = nums1.length, n = nums2.length;
        Map<Long, Integer> map1 = new HashMap<>();
        Map<Long, Integer> map2 = new HashMap<>();
        long[] dp1 = new long[m + 1];
        long[] dp2 = new long[n + 1];
        int i = 1, j = 1;
        while(i <= m || j <= n) {
            if(i > m) {
                dp2[j] = Math.max(dp2[j - 1] + (long)nums2[j - 1], dp1[map1.getOrDefault((long)nums2[j - 1], 0)]);
                j++;
            } else if(j > n) {
                dp1[i] = Math.max(dp1[i - 1] + (long)nums1[i - 1], dp2[map2.getOrDefault((long)nums1[i - 1], 0)]);
                i++;
            } else {
                if(nums1[i - 1] < nums2[j - 1]) {
                    dp1[i] = Math.max(dp1[i - 1] + (long)nums1[i - 1], dp2[map2.getOrDefault((long)nums1[i - 1], 0)]);
                    map1.put((long)nums1[i - 1], i);
                    i++;
                } else if((nums1[i - 1] > nums2[j - 1])) {
                    dp2[j] = Math.max(dp2[j - 1] + (long)nums2[j - 1], dp1[map1.getOrDefault((long)nums2[j - 1], 0)]);
                    map2.put((long)nums2[j - 1], j);
                    j++;
                } else {
                    long a = Math.max(dp1[i - 1] + (long)nums1[i - 1], dp2[map2.getOrDefault((long)nums1[i - 1], 0)]);
                    long b = Math.max(dp2[j - 1] + (long)nums2[j - 1], dp1[map1.getOrDefault((long)nums2[j - 1], 0)]);
                    long max = Math.max(a, b);
                    dp1[i] = max;
                    dp2[j] = max;
                    i++;
                    j++;
                }
            }
        }
        return (int)(Math.max(dp1[m], dp2[n]) % MOD);
    }
}
