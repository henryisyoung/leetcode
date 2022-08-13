package uber.phone.leetcode;

import java.util.Arrays;

public class CountInversions {
    // https://www.geeksforgeeks.org/counting-inversions/
    public static int countInversions(int[] array) {
        return counterHelper(array, 0, array.length - 1);
    }

    private static int counterHelper(int[] array, int l, int r) {
        if (l >= r) {
            return 0;
        }
        int mid = l + (r - l) / 2;
        int count = 0;
        count += counterHelper(array, l, mid);
        count += counterHelper(array, mid + 1, r);
        count += counter(array, l, mid, r);
        return count;
    }

    private static int counter(int[] array, int l, int mid, int r) {
        int count = 0;
        int size = r - l + 1;
        int[] copy = new int[size];
        int index = 0, i = l, j = mid + 1;
        while (i <= mid && j <= r) {
            if (array[i] <= array[j]) {
                copy[index++] = array[i++];
            } else {
                count += mid - i + 1;
                copy[index++] = array[j++];
            }
        }
        while (i <= mid) {
            copy[index++] = array[i++];
        }
        while ( j <= r) {
            copy[index++] = array[j++];
        }
        for (int k = 0; k < size; k++) {
            array[k + l] = copy[k];
        }
        return count;
    }

    public static void mergeSort(int[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        mergeSortHelper(array, 0 , array.length - 1);
    }

    private static void mergeSortHelper(int[] array, int l, int r) {
        if (l >= r) {
            return;
        }
        int mid = l + (r - l) / 2;
        mergeSortHelper(array, l ,mid);
        mergeSortHelper(array, mid + 1, r);
        merger(array, l, mid, r);
    }

    private static void merger(int[] array, int l, int mid, int r) {
        int[] copy = new int[r - l + 1];
        int i = l, j = mid + 1;
        int index = 0;
        while (i <= mid && j <= r) {
            copy[index++] = array[i] <= array[j] ? array[i++] : array[j++];
        }
        while (i <= mid) {
            copy[index++] = array[i++];
        }
        while (j <= r) {
            copy[index++] = array[j++];
        }
        for (int k = 0; k < copy.length; k++) {
            array[k + l] = copy[k];
        }
    }

    public static void main(String[] args) {
        int[] array = {3,1,2};
        System.out.println(countInversions(array));
        System.out.println(Arrays.toString(array));
    }
}
