package recovery;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class TimeMap {

    private Map<String, TreeMap<Integer, String>> map;
    public TimeMap() {
        map = new HashMap<>();
    }

    public void set(String key, String value, int timestamp) {
        map.putIfAbsent(key, new TreeMap<>());
        map.get(key).put(timestamp, value);
    }

    public String get(String key, int timestamp) {
        if (!map.containsKey(key)) {
            return "";
        }
        TreeMap<Integer, String> timeStampMap = map.get(key);
        Integer floor = timeStampMap.floorKey(timestamp);
        return floor == null ? "" : timeStampMap.get(floor);
    }
}
