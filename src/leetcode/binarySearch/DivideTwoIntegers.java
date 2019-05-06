package leetcode.binarySearch;

public class DivideTwoIntegers {
    public static int divide(int dividend, int divisor) {
        if (divisor == 0) {
            return -1;
        }
        if (dividend == 0) {
            return 0;
        }
        boolean positive = true;
        if (dividend > 0 && divisor < 0 || divisor > 0 && dividend < 0) {
            positive = false;
        }
        int did = Math.abs(dividend), dor = Math.abs(divisor);
        if (did < dor) {
            return 0;
        }
        long result = 0;
        while (did >= dor) {
            int temp = dor;
            int count = 1;
            while (did >= temp) {
                temp *= 2;
                count *= 2;
            }
            temp /= 2;
            count /= 2;
            result += count;
            did -= temp;
        }
        if (!positive) {
            return (int) -result;
        }
        return result == Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) result;
    }

    public static void main(String[] args) {
        System.out.println(divide(10, -2));
    }

}
