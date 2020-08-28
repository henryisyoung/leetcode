package apple;

import java.util.HashMap;
import java.util.Map;

public class FrogJump {
    public boolean canCross(int[] stones) {
        Map<String, Boolean> map = new HashMap<>();
        return canCrossHelper(map, 0, 0, stones);
    }

    private boolean canCrossHelper(Map<String, Boolean> map, int jump, int pos, int[] stones) {
        if (pos == stones.length - 1) return true;
        String key = jump + "#" + pos;
        if (map.containsKey(key)) return map.get(key);
        for (int next = pos + 1; next < stones.length; next++) {
            int dist = stones[next] - stones[pos];
            if (dist < jump - 1) continue;
            if (dist > jump + 1) {
                map.put(key, false);
                return false;
            }
            if (canCrossHelper(map, dist, next, stones)) {
                map.put(key, true);
                return true;
            }
        }
        map.put(key, false);
        return false;
    }
}
