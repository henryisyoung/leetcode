package facebook;

import java.util.HashMap;
import java.util.Map;

public class _FrogJump {
    public boolean canCross(int[] stones) {
        if (stones == null || stones.length == 0) return true;
        Map<String, Boolean> map = new HashMap<>();
        return canCrossHelper(map, 0, 0, stones);
    }

    private boolean canCrossHelper(Map<String, Boolean> map, int pos, int jump, int[] stones) {
        String key = pos + "," + jump;
        if (pos == stones.length - 1) {
            map.put(key, true);
            return true;
        }
        if (map.containsKey(key)) return map.get(key);
        for (int i = pos + 1; i < stones.length; i++) {
            int dist = stones[i] - stones[pos];
            if (dist < jump - 1) continue;
            if (dist > jump + 1) {
                map.put(key, false);
                return false;
            }
            if (canCrossHelper(map, i, dist, stones)) {
                map.put(key, true);
                return true;
            }
        }
        map.put(key, false);
        return false;
    }
}
