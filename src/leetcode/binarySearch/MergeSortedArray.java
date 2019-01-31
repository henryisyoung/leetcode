package leetcode.binarySearch;

public class MergeSortedArray {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int i = m - 1, j = n - 1, index = m + n - 1;
        while (i >= 0 && j >= 0){
            if(nums1[i] > nums2[j]){
                nums1[index] = nums1[i];
                i--;
            }else {
                nums1[index] = nums2[j];
                j--;
            }
            index--;
        }
        while(j >= 0){
            nums1[index--] = nums2[j--];
        }
    }
    public void merge2(int[] nums1, int m, int[] nums2, int n) {
        int i = m - 1, j = n - 1;
        int pos = i + j - 1;
        while (i >= 0 && j >= 0) {
            if(nums1[i] > nums2[j]) {
                nums1[pos--] = nums1[i--];
            } else {
                nums1[pos--] = nums2[j--];
            }
        }
        while (j >= 0) {
            nums1[pos--] = nums2[j--];
        }
    }
}
