package quora;

import java.util.*;

public class GrayCode {
    public List<Integer> grayCode(int n) {
        List<Integer> result = new ArrayList<>();
        if (n < 0) {
            return result;
        }
        if (n == 0) {
            result.add(0);
            return result;
        }
        List<Integer> prev = grayCode(n - 1);
        int val = 1 << (n - 1);
        result.addAll(prev);
        for (int i = prev.size() - 1; i >=0; i--) {
            result.add(val + prev.get(i));
        }
        return result;
    }
}
