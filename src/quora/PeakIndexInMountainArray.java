package quora;

public class PeakIndexInMountainArray {
    public int peakIndexInMountainArray(int[] A) {
        int start = 0, end = A.length - 1;
        while (start + 1 < end) {
            int mid = start + (end - start) / 2;
            if (A[mid] > A[mid - 1] && A[mid] > A[mid + 1]) {
                return mid;
            } else if (A[mid] > A[mid - 1] && A[mid] < A[mid + 1]){
                start = mid;
            } else {
                end = mid;
            }
        }
        if (A[start] > A[start - 1] && A[start] > A[start + 1]){
            return start;
        }
        if (A[end] > A[end - 1] && A[end] > A[end + 1]){
            return start;
        }
        return -1;
    }
}
