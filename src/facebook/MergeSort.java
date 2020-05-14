package facebook;

import java.util.Arrays;

public class MergeSort {
    public static String mergeSort(String s) {
        if (s == null || s.length() == 0) return s;
        char[] arr = s.toCharArray();
        int left = 0, right = arr.length - 1;
        sort(arr, left, right);
        return new String(arr);
    }

    private static void sort(char[] arr, int l, int r) {
        if (l < r) {
            // Find the middle point
            int m = (l+r)/2;

            // Sort first and second halves
            sort(arr, l, m);
            sort(arr , m+1, r);

            // Merge the sorted halves
            merge(arr, l, m, r);
        }
    }

    private static void merge(char[] arr, int l, int m, int r) {
        int s1 = l, s2 = m + 1, index = 0;
        char[] copy = new char[r - l + 1];
        while (s1 <= m && s2 <= r) {
            copy[index++] = arr[s1] < arr[s2] ? arr[s1++] : arr[s2++];
        }
        while (s1 <= m) {
            copy[index++] = arr[s1++];
        }
        while (s2 <= r) {
            copy[index++] = arr[s2++];
        }
        System.out.println(Arrays.toString(copy));
        System.arraycopy(copy, 0, arr, l, copy.length);
    }

    public static void main(String[] args) {
        String s = "cids";
        System.out.println(mergeSort(s));
    }
}
