package datastructure.binarySearch;

public class Sqrt {
    public int mySqrt(int x) {
        if(x <= 1)
            return x;

        int left = 0, right = x;

        while (left + 1 < right) {
            int mid = left + (right - left) / 2;

            if (x / mid == mid) return mid;
            else if (mid < x / mid) {
                left = mid;
            } else {
                right = mid;
            }

        }

        return left;
    }
}
