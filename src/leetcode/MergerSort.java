package leetcode;

import java.util.Arrays;

public class MergerSort {
    public static void main(String[] args) {
        int[] a = {2, 6, 3, 5, 1};
        mergeSort(a);
        System.out.println(Arrays.toString(a));
    }

    public static void mergeSort(int[] a) {
        int right = a.length - 1;
        mergeSortHelper(a, 0, right);
    }

    private static void mergeSortHelper(int[] a, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSortHelper(a, left, mid);
            mergeSortHelper(a, mid + 1, right);
            merge(a, left, right, mid);
        }
    }

    private static void merge(int[] a, int left, int rightEnd, int mid) {
        int right = mid + 1, leftEnd = mid;
        int num = rightEnd - left + 1, n = a.length, t = left;
        int[] temp = new int[n];
        while (left <= leftEnd && right <= rightEnd) {
            if (a[left] < a[right]) {
                temp[t++] = a[left++];
            } else {
                temp[t++] = a[right++];
            }
        }
        while (left <= leftEnd){
            temp[t++] = a[left++];
        }
        while (right <= rightEnd) {
            temp[t++] = a[right++];
        }
        while (num > 0) {
            num--;
            a[rightEnd] = temp[rightEnd];
            rightEnd--;
        }
    }
}
