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
    public static List<Integer> maxStepsForRange(int n) {
        if (n < 1) {
            return null;
        }
        List<Integer> result = new ArrayList<>();
        Map<Integer, Integer> map = new HashMap<>();
        map.put(1,1);
        for (int i = 1; i <= n; i++) {
            int count = findSteps(i, map);
            map.put(i, count);
            result.add(count);
        }
        return result;
    }

    private static int findSteps(int i, Map<Integer, Integer> map) {
        if (map.containsKey(i)) {
            return map.get(i);
        }
        return i % 2 == 0 ? 1 + findSteps(i/2, map) : 1 + findSteps(3*i + 1, map);
    }

    public static int findCollatzConjecture(int n) {
        if (n == 1) {
            return 1;
        }
        return n % 2 == 0 ? 1 + findCollatzConjecture(n/2) : 1 + findCollatzConjecture(3*n + 1);
    }

    public static void main(String[] args) {
        int n = 10;
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            list.add(findCollatzConjecture(i));
        }
        List<Integer> result = maxStepsForRange(n);
        System.out.println(list);
        System.out.println(result);
    }
}
