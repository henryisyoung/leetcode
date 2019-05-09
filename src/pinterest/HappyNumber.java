package pinterest;

import java.util.*;

public class HappyNumber {
    public boolean isHappy(int n) {
        if (n <= 0) {
            return false;
        }
        Set<Integer> set = new HashSet<>();
        set.add(n);
        while (n != 1) {
            n = next(n);
            if (set.contains(n)){
                return false;
            }
            set.add(n);
        }
        return true;
    }

    private int next(int n) {
        int result = 0;
        while (n > 0) {
            result += (n % 10) * (n % 10);
            n /= 10;
        }
        return result;
    }
}
