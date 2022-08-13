package rippling;

import java.util.HashMap;
import java.util.Map;

public class UrlHitter2 {

    class HitCounter {
        long[] timer, count;
        public HitCounter() {
            this.count = new long[300];
            this.timer = new long[300];
        }

        public void add(long timestamp) {
            int index = (int) (timestamp % 300);
            if (timer[index] == 0) {
                count[index]++;
                timer[index] = timestamp;
            } else {
                if (timer[index] == timestamp) {
                    count[index]++;
                } else {
                    count[index] = 1;
                }
            }
            timer[index] = timestamp;
        }

        public long count(long timestamp) {
            long sum = 0;
            for (int i = 0; i < 300; i++) {
                if(timer[i] + 300 >= timestamp) {
                    sum += count[i];
                }
            }
            return sum;
        }
    }
    Map<String, HitCounter> map;

    public UrlHitter2() {
        this.map = new HashMap<>();
    }
    public void addEvent(String url, long timestamp) {
        map.putIfAbsent(url, new HitCounter());
        map.get(url).add(timestamp / 1000);
    }

    public long queryEvent(String url, long timestamp) {
        if (!map.containsKey(url)) {
            return 0;
        }
        return map.get(url).count(timestamp / 1000);
    }
}
