package twitter;

import java.util.*;

public class FrogJump {
    public boolean canCross(int[] stones) {
        Map<Integer, Boolean> map = new HashMap<>();
        return canCrossHelper(0, 0, map, stones);
    }

    private boolean canCrossHelper(int pos, int jump, Map<Integer, Boolean> map, int[] stones) {
        int n = stones.length, key = pos << 11 | jump;
        if (pos >= n - 1) {
            map.put(key, true);
            return true;
        }
        if (map.containsKey(key)) {
            return map.get(key);
        }
        for (int i = pos + 1; i < n; i++) {
            int dist = stones[i] - stones[pos];
            if (dist < jump - 1) {
                continue;
            }
            if (dist > jump + 1) {
                map.put(key, false);
                return false;
            }
            if (canCrossHelper(i, dist, map, stones)) {
                map.put(key, true);
                return true;
            }
        }
        map.put(key, false);
        return false;
    }
}
