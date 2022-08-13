package uber.phone;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class UrlCounter {
    Map<String, Deque<Long>> map;
    int total;
    public UrlCounter() {
        this.map = new HashMap<>();
        this.total = 0;
    }

    public void count(String a, long time) {
        map.putIfAbsent(a, new LinkedList<>());
        Deque<Long> deque = map.get(a);
        total++;
        deque.add(time);
        while (!deque.isEmpty() && deque.peekFirst() + 300  < time) {
            total--;
            deque.pollFirst();
        }
        map.put(a, deque);
    }

    public int getTotal(long time) {
        for (Deque<Long> deque : map.values()) {
            while (!deque.isEmpty() && deque.peekFirst() + 300  < time) {
                total--;
                deque.pollFirst();
            }
        }
        return total;
    }

    public int get(String a, long time) {
        if (!map.containsKey(a)) {
            return 0;
        }
        Deque<Long> deque = map.get(a);
        while (!deque.isEmpty() && deque.peekFirst() + 300  < time) {
            total--;
            deque.pollFirst();
        }
        map.put(a, deque);
        return map.get(a).size();
    }

    public static void main(String[] args) {
        UrlCounter counter = new UrlCounter();
        counter.count("a", 1647718624);
        counter.count("a", 1647718630);
        counter.count("b", 1647718730);
        System.out.println(counter.get("a", 1647718730));
        System.out.println(counter.getTotal(1647718731));
        System.out.println(counter.get("a", 1647718925));
        System.out.println(counter.getTotal(1647718925));

    }

}
