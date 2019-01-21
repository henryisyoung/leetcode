package leetcode;

public class Question88_Sorted_Array {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int i = m - 1;
        int j = n - 1;
        int index = m + n - 1;
        while(i >= 0 && j >= 0){
            if(nums1[i] < nums2[j]){
                nums1[index] = nums2[j];
                j--;
            }else {
                nums1[index] = nums1[i];
                i--;
            }
            index--;
        }
        while(j >= 0){
            nums1[index--] = nums2[j--];
        }
    }
}
