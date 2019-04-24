package leetcode;

import java.util.Arrays;

public class QuickSort {
    public static void main(String[] args) {
        int[] a = {2, 6, 3, 5, 1};
        quickSort(a);
        System.out.println(Arrays.toString(a));
    }

    public static void quickSort(int[] a) {
        int right = a.length - 1;
        quickSortHelper(a, 0, right);
    }

    private static void quickSortHelper(int[] a, int left, int right) {
        if (left < right) {
            int pos = partition(a, left, right);
            quickSortHelper(a, left, pos - 1);
            quickSortHelper(a, pos + 1, right);
        }
    }

    private static int partition(int[] a, int left, int right) {
        int pivot = a[left];
        int l = left, r = right;
        while (l < r) {
            while (l < r && a[r] >= pivot) {
                r--;
            }
            a[l] = a[r];
            while (l < r && a[l] <= pivot) {
                l++;
            }
            a[r] = a[l];
        }
        a[l] = pivot;
        return l;
    }
}
