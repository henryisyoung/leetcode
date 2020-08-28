package apple;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class TimeMap {
    Map<String, TreeMap<Integer, String>> map;
    public TimeMap() {
        this.map = new HashMap<>();
    }

    public void set(String key, String value, int timestamp) {
        map.putIfAbsent(key, new TreeMap<>());
        map.get(key).put(timestamp, value);
    }

    public String get(String key, int timestamp) {
        if (!map.containsKey(key)) return "";
        TreeMap<Integer, String> list = map.get(key);
        Integer t = list.floorKey(timestamp);
        return t == null ? "" : list.get(t);
    }
}
