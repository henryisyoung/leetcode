package leetcode.binarySearch;

public class Sqrt {
    public int mySqrt(int x) {
        if(x == 0){
            return 0;
        }
        int low = 1, high = x;
        while (low + 1 < high){
            int mid = low + (high - low)/2;
            if(mid == x/mid){
                return mid;
            } else if(mid > x/mid){
                high = mid;
            }else {
                low =mid;
            }
        }
        return low;
    }
    public int mySqrt2(int x) {
        if(x == 0) return 0;
        int start = 1, end = x;
        while (start + 1 < end) {
            int mid = start + (end - start)/2;
            if(mid == x/mid) {
                return mid;
            } else if (mid > x/mid) {
                end = mid;
            } else {
                start = mid;
            }
        }
        return start;
    }
}
