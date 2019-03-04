package airbnb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollatzConjecture {
    /**
     * 记忆化搜索 找到下一步 把问题理解清楚
     * @param n the upper bound of range the lower bound is 1
     * @return from 1 to n what is the max length to get final Collatz Conjecture "1"
     */
    public static int maxStepsForRange(int n) {
        if (n < 1) {
            return -1;
        }
        Integer result = -1;
        Map<Integer, Integer> map = new HashMap<>();
        map.put(1, 1);
        for (int i = 1; i <= n; i++) {
            int max = memoSearch(map, i);
            map.put(i, max);
            result = Math.max(max, result);
        }
        return result;
    }

    private static int memoSearch(Map<Integer, Integer> map, int i) {
        if (map.containsKey(i)) {
            return map.get(i);
        }
        return i % 2 == 0 ? 1 + memoSearch(map, i/2) : 1 + memoSearch(map, 3 * i + 1);
    }

    public static int findCollatzConjecture(int n) {
        if (n == 1) {
            return 1;
        }
        return n % 2 == 0 ? 1 + findCollatzConjecture(n/2) : 1 + findCollatzConjecture(3*n + 1);
    }

    public static void main(String[] args) {
        int n = 100;
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            list.add(findCollatzConjecture(i));
        }
        System.out.println(list.toString());
        System.out.println(maxStepsForRange(n));
    }
}
