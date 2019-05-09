package pinterest;

import java.util.Map;

public class MedianTwoSortedArrays {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int m = nums1.length, n = nums2.length;
        int len = m + n;
        if (len % 2 == 0) {
            return 0.5 * (medianHelper(nums1, 0, nums2, 0, len / 2) +
                    medianHelper(nums1, 0, nums2, 0, len / 2 + 1));
        } else {
            return (double) medianHelper(nums1, 0, nums2, 0, len / 2 + 1);
        }
    }

    private int medianHelper(int[] nums1, int aStart, int[] nums2, int bStart, int k) {
        if (aStart >= nums1.length) {
            return nums2[bStart + k - 1];
        }
        if (bStart >= nums2.length) {
            return nums1[aStart + k - 1];
        }
        if (k == 1) {
            return Math.min(nums1[aStart], nums2[bStart]);
        }
        int aVal = aStart + k/2 - 1 >= nums1.length ? Integer.MAX_VALUE : nums1[aStart + k/2 - 1];
        int bVal = bStart + k/2 - 1 >= nums2.length ? Integer.MAX_VALUE : nums2[bStart + k/2 - 1];
        if (aVal < bVal) {
            return medianHelper(nums1, aStart + k/2, nums2, bStart, k - k/2);
        } else {
            return medianHelper(nums1, aStart, nums2, bStart + k/2, k - k/2);
        }
    }
}
