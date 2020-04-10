package facebook;

import java.util.ArrayList;
import java.util.List;

public class ExpressOperator {
    public static List<String> cal(int[] A, int target) {
        List<String> result = new ArrayList<>();
        if (A == null || A.length == 0) return result;
        findAll(A, target, 0, "", 0, result);
        return result;
    }

    private static void findAll(int[] a, int target, int pos, String s, int sum, List<String> result) {
        if (pos == a.length) {
            if (sum == target) result.add(s);
            return;
        }
        int cur = a[pos];
        findAll(a, target, pos + 1, s + "-" + cur, sum - cur, result);
        if (pos == 0) {
            findAll(a, target, pos + 1, s + cur, sum + cur, result);
        } else {
            findAll(a, target, pos + 1, s + "+" + cur, sum + cur, result);
        }

    }

    public static void main(String[] args) {
        System.out.println(cal(new int[]{1,2,3}, -2));
    }

}
