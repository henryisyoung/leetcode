package apple;

public class MedianTwoSortedArrays {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int m = nums1.length, n = nums2.length;
        if ((m + n) % 2 == 0) {
            return 0.5 * (helper(nums1, 0, nums2, 0, (m + n) / 2 ) + helper(nums1, 0, nums2, 0, (m + n) / 2 + 1));
        } else {
            return helper(nums1, 0, nums2, 0, (m + n) / 2 + 1);
        }
    }

    private int helper(int[] nums1, int s1, int[] nums2, int s2, int k) {
        int m = nums1.length, n = nums2.length;
        if (s1 >= m) {
            return nums2[s2 + k - 1];
        }
        if (s2 >= n) {
            return nums1[s1 + k - 1];
        }
        if(k == 1) {
            return Math.min(nums1[s1], nums2[s2]);
        }
        int val1 = s1 + k / 2 - 1 >= m ? Integer.MAX_VALUE : nums1[s1 + k / 2 - 1] ;
        int val2 = s2 + k / 2 - 1 >= n ? Integer.MAX_VALUE : nums2[s2 + k / 2 - 1] ;

        if (val1 < val2) {
            return helper(nums1, s1 + k/2, nums2, s2, k - k/2);
        } else {
            return helper(nums1, s1 , nums2, s2 + k/2, k - k/2);
        }
    }
}
