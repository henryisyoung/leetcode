package Twitter;

import java.util.*;

public class MyCalendarI {
    TreeMap<Integer, Integer> calendar;
    public MyCalendarI() {
        this.calendar = new TreeMap<>();
    }

    public boolean book(int start, int end) {
        Integer prev = calendar.floorKey(start);
        Integer next = calendar.ceilingKey(start);
        if (prev != null && calendar.get(prev) > start || next != null && next < end) {
            return false;
        }
        calendar.put(start, end);
        return true;
    }
}
