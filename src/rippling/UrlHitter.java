package rippling;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class UrlHitter {

    Map<String, Deque<Long>> map;

    public UrlHitter() {
        this.map = new HashMap<>();
    }
    public void addEvent(String url, long timestamp) {
        map.putIfAbsent(url, new LinkedList<>());
        Deque<Long> deque = map.get(url);

        while (!deque.isEmpty() && deque.peekFirst() + 5 * 60 * 1000 < timestamp) {
            deque.pollFirst();
        }
        deque.add(timestamp);
    }

    public int queryEvent(String url, long timestamp) {
        if (!map.containsKey(url)) {
            return 0;
        }
        Deque<Long> deque = map.get(url);
        while (!deque.isEmpty() && deque.peekFirst() + 5 * 60 * 1000 < timestamp) {
            deque.pollFirst();
        }
        return deque.size();
    }
}
