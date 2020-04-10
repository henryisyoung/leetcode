package facebook;

import java.util.ArrayList;
import java.util.List;

public class ExpressOperatorMore {
    public static List<String> cal(int[] A, int target) {
        List<String> result = new ArrayList<>();
        if (A == null || A.length == 0) return result;
        findAll(A, target, 0, "", 0, result, 0);
        return result;
    }

    private static void findAll(int[] a, int target, int pos, String s, int sum, List<String> result, int prev) {
        if (pos == a.length) {
            if (sum == target) result.add(s);
            return;
        }
        int cur = a[pos];
        findAll(a, target, pos + 1, s + "-" + cur, sum - cur, result, prev);
        if (pos == 0) {
            findAll(a, target, pos + 1, s + cur, sum + cur, result, prev);
        } else {
            findAll(a, target, pos + 1, s + "*" + cur, sum - prev + prev * cur, result, prev * cur);
            if (cur != 0) {
                findAll(a, target, pos + 1, s + "/" + cur, sum - prev + prev / cur, result, prev / cur);
            }
        }

    }

    public static List<String> addOperators(String num, int target) {
        List<String> result = new ArrayList<>();
        findAllComb(num, target, result, "", 0, 0, 0);
        return result;
    }

    private static void findAllComb(String num, int target, List<String> result, String s, long prev, int pos, long t) {
        if (pos == num.length()) {
            if (t == target) result.add(s);
            return;
        }
        for (int i = pos + 1; i <= num.length(); i++) {
            String str = num.substring(pos, i);
            if (str.charAt(0) == '0' && str.length() > 1) break;
            long val = Long.parseLong(str);
            if (pos == 0) {
                findAllComb(num, target, result, s + val, val, i, t + val);
            } else {
                findAllComb(num, target, result, s + "+" + val, val, i, t + val);
                findAllComb(num, target, result, s + "-" + val, - val, i, t - val);
                findAllComb(num, target, result, s + "*" + val, prev * val, i, t - prev + prev * val);
            }
        }
    }

    public static void main(String[] args) {
//        System.out.println(cal(new int[]{1,2,3}, -2));
        System.out.println(addOperators("232", 8));
    }

}
