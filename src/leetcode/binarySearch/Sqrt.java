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

    public double mySqrt2(int x) {
        double l = 0;
        double r = Math.max(x, 1.0);
        double eps = 1e-12;

        while (l + eps < r) {
            double mid = l + (r - l) / 2;
            if (mid * mid < x) {
                l = mid;
            } else {
                r = mid;
            }
        }
        return l;
    }
}
