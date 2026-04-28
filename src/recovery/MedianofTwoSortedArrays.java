package recovery;

public class MedianofTwoSortedArrays {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int len = nums1.length + nums2.length;
        if (len % 2 == 1) {
            return findKthSmallest(nums1, 0, nums2, 0, len / 2 + 1);
        } else {
            return 0.5 * (findKthSmallest(nums1, 0, nums2, 0, len / 2 + 1) + findKthSmallest(nums1, 0, nums2, 0, len / 2));
        }
    }

    private double findKthSmallest(int[] nums1, int index1, int[] nums2, int index2, int k) {
        if (index1 >= nums1.length) {
            return nums2[index2 + k - 1];
        }
        if (index2 >= nums2.length) {
            return nums1[index1 + k - 1];
        }
        if (k == 1) {
            return Math.min(nums1[index1], nums2[index2]);
        }

        int val1 = index1 + k / 2 - 1 >= nums1.length ? Integer.MAX_VALUE : nums1[index1 + k/2 - 1];
        int val2 = index2 + k / 2 - 1 >= nums2.length ? Integer.MAX_VALUE : nums2[index2 + k / 2 - 1];
        if (val1 < val2) {
            return findKthSmallest(nums1, index1 + k / 2, nums2, index2, k - k/2);
        }
        return findKthSmallest(nums1, index1, nums2, index2 + k/2, k - k/2);
    }
}
