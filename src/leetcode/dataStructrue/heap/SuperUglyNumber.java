package leetcode.dataStructrue.heap;

import java.util.Arrays;

public class SuperUglyNumber {
    public static int nthSuperUglyNumber(int n, int[] primes) {
        int[] times = new int[primes.length];
        int[] uglys = new int[n];
        uglys[0] = 1;
        int pos = -1;
        for (int i = 1; i < n; i++) {
            int min = Integer.MAX_VALUE;
            for (int j = 0; j < primes.length; j++) {
                min = Math.min(min, primes[j] * uglys[times[j]]);
            }
            uglys[i] = min;
            for (int j = 0; j < times.length; j++) {
                if (uglys[times[j]] * primes[j] == min) {
                    times[j]++;
                }
            }
        }
        System.out.println(Arrays.toString(uglys));
        return uglys[n - 1];
    }

    public static void main(String[] args) {
        int n = 12;
        int[] primes = {2,7,13,19};
        nthSuperUglyNumber(n, primes);
    }
}
