package google.vo;

import java.util.Map;
import java.util.TreeMap;

public class MyCalendar {
    TreeMap<Integer, Integer> map;

    public MyCalendar() {
        this.map = new TreeMap<>();
    }

    public boolean book(int start, int end) {
        Map.Entry<Integer, Integer> floor = map.floorEntry(start), ceil = map.ceilingEntry(start);
        if (floor != null) {
            if (floor.getValue() > start) {
                return false;
            }
        }
        if (ceil != null) {
            if (ceil.getKey() < end) {
                return false;
            }
        }
        map.put(start, end);
        return true;
    }
}
