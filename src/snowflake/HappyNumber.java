package snowflake;

import java.util.HashSet;
import java.util.Set;

public class HappyNumber {
    public boolean isHappy(int n) {
        Set<Integer> visited = new HashSet<>();

        return isHappyHelper(n, visited);
    }

    private boolean isHappyHelper(int n, Set<Integer> visited) {
        if (n == 1) return true;
        if (visited.contains(n)) return false;
        visited.add(n);
        int val = 0;
        while (n > 0) {
            val += (n % 10) * (n % 10);
            n /= 10;
        }
        return isHappyHelper(val, visited);
    }
}
