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
}
