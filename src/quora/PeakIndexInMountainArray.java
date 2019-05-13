package quora;

public class PeakIndexInMountainArray {
    public int peakIndexInMountainArray(int[] A) {
        int n = A.length;
        int left = 0, right = n - 1;
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (A[mid] > A[mid - 1] && A[mid] > A[mid + 1]) {
                return mid;
            } else if (A[mid] > A[mid - 1] && A[mid] < A[mid + 1]) {
                left = mid;
            } else {
                right = mid;
            }
        }
        if (A[left] > A[left - 1] && A[left] > A[left + 1]) {
            return left;
        }
        if (A[right] > A[right - 1] && A[right] > A[right + 1]) {
            return right;
        }
        return -1;
    }
}
