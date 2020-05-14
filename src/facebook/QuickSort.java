package facebook;

public class QuickSort {
    public static String quickSort(String s) {
        if(s == null || s.length() == 0) return s;
        int left = 0, right = s.length() - 1;
        char[] arr = s.toCharArray();
        sort(arr, left, right);
        return new String(arr);
    }

    private static void sort(char[] arr, int left, int right) {
        if (left >= right) return;
        int pos = partition(arr, left, right);
        sort(arr, left, pos - 1);
        sort(arr, pos + 1, right);
    }

    private static int partition(char[] arr, int left, int right) {
        char pivot = arr[left];
        int l = left, r = right;
        while (l < r) {
            while (l < r && arr[r] >= pivot) {
                r--;
            }
            arr[l] = arr[r];
            while (l < r && arr[l] < pivot) {
                l++;
            }
            arr[r] = arr[l];
        }
        arr[l] = pivot;
        return l;
    }

    public static void main(String[] args) {
        String s = "abcae";
//        System.out.println(quickSort(s));
        System.out.println(s.indexOf('a', 1));
    }
}
