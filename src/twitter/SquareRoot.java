package twitter;

public class SquareRoot {
    //题目是求一个数的squareroot。答案不一定是整数，input可以有一个precision。二分就行
    public static double mySqrt(int x, double precision) {
        double left = 1.0, right = (double) x;
        while (left < right) {
            double mid = left + (right - left) / 2;
            if (Math.abs(x/mid - mid) < precision) {
                return mid;
            } else if (x/mid > mid) {
                left = mid;
            } else {
                right = mid;
            }
        }
        return left;
    }

    public static void main(String[] args) {
        int n = 100000;
        System.out.println(mySqrt(n, 0.00001));
        System.out.println(Math.sqrt(n));
    }
}
