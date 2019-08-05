package uber;

import java.util.*;

public class SplitArrayWithSameAverage {
    public static boolean splitArraySameAverage(int[] A) {
        if (A == null || A.length == 0) return false;
        int n = A.length, sum = 0, m = n / 2;
        for (int i : A) sum += i;
        boolean possible = false;
        for (int i = 1; i <= m; i++) {
            if (sum * i % n == 0) {
                possible = true;
                break;
            }
        }
        if (!possible) return false;
        ArrayList<HashSet<Integer>> dp = new ArrayList<>();
        for (int i = 0; i <= m; i++) dp.add(new HashSet<>());

        dp.get(0).add(0);
        for (int num : A) {
            for (int i = m; i >= 1; --i) {
                for (int a : dp.get(i - 1)) {
                    dp.get(i).add(a + num);
                }
//                System.out.println("i " + i + " set " + dp.get(i).toString());
            }
        }
        for (int i = 1; i <= m; ++i) {
            if (sum * i % n == 0 && dp.get(i).contains(sum * i / n)) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        int[] A = {1,2,3,4};
        System.out.println(splitArraySameAverage(A));
    }
}
