package leetcode.twoPointer;

public class UglyNumberII {
    public int nthUglyNumber(int n) {
        int[] table = new int[n];
        int pos = 1, i = 0, j = 0, k = 0;
        table[0] = 1;
        while (pos < n) {
            int a = table[i] * 2, b = table[j] * 3, c = table[k] * 5;
            int min = Math.min(a, Math.min(b, c));
            if (min == a) {
                i++;
            } else if (min == b) {
                b++;
            } else {
                c++;
            }
            table[pos++] = min;
        }

        return table[n - 1];
    }
}
