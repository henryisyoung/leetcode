package snap;

import java.util.*;

public class KeyValue {
    //https://www.1point3acres.com/bbs/thread-526562-1-1.html
    Map<Integer, Map<Integer, Integer>> map;
    int version;
    public KeyValue() {
        this.version = 0;
        this.map = new HashMap<>();
    }

    public void set(int k, int v) {
        map.putIfAbsent(version, new HashMap<>());
        Map<Integer, Integer> localMap = map.get(version);
        localMap.put(k, v);
    }

    public int get(int k) {
        if (map.containsKey(version)) {
            Map<Integer, Integer> localMap = map.get(version);
            if (localMap.containsKey(k)) return localMap.get(k);
        }
        return -1;
    }
}
