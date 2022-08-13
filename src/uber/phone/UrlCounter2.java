package uber.phone;

import apple.HitCounter;

import java.util.HashMap;
import java.util.Map;

public class UrlCounter2 {

    Map<String, HitCounter> map;
    public UrlCounter2() {
        this.map = new HashMap<>();
    }

    public void count(String a, int time) {
        map.putIfAbsent(a, new HitCounter());
        map.get(a).hit( time);
    }

    public int getTotal(int time) {
        int val = 0;
        for (HitCounter counter : map.values()) {
            val += counter.getHits(time);
        }
        return val;
    }

    public int get(String a, int time) {
        if (!map.containsKey(a)) {
            return 0;
        }
        return map.get(a).getHits(time);
    }

    public static void main(String[] args) {
        UrlCounter2 counter = new UrlCounter2();
        counter.count("a", 1647718624);
        counter.count("a", 1647718630);
        counter.count("b", 1647718730);
        System.out.println(counter.get("a", 1647718730));
        System.out.println(counter.getTotal(1647718731));
        System.out.println(counter.get("a", 1647718925));
        System.out.println(counter.getTotal(1647718925));
    }

}
