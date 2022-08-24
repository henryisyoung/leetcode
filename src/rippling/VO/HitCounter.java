package rippling.VO;

import java.util.HashMap;
import java.util.Map;

public class HitCounter {
    class Counter {
        int[] counter;
        long[] timer;
        int size;
        public Counter(int size) {
            this.counter = new int[size];
            this.timer = new long[size];
            this.size = size;
        }

        public void set(long timeStamp) {
            int index = (int) timeStamp % size;
            if (timer[index] == 0) {
                timer[index] = timeStamp;
                counter[index] = 1;
            } else {
                if (timer[index] == timeStamp) {
                    counter[index]++;
                } else {
                    timer[index] = timeStamp;
                    counter[index] = 1;
                }
            }
        }

        public int count(long timeStamp) {
            int count = 0;
            for (int i = 0; i < size; i++) {
                if (timer[i] + size >= timeStamp) {
                    count += counter[i];
                }
            }
            return count;
        }
    }
    Map<String, Counter> map;
    int size;
    public HitCounter(int size) {
        this.map = new HashMap<>();
        this.size = size;
    }

    public void set(String url, long timeStamp) {
        Counter counter = map.getOrDefault(url, new Counter(size));
        counter.set(timeStamp);
        map.put(url, counter);
    }

    public int count(String url, long timeStamp) {
        if (!map.containsKey(url)) {
            return 0;
        }
        Counter counter = map.get(url);
        return counter.count(timeStamp);
    }

    public static void main(String[] args) {
        HitCounter hitCounter = new HitCounter(300);
        hitCounter.set("abc", 100);
        hitCounter.set("abc", 201);
        hitCounter.set("abc", 202);

        hitCounter.set("xyz", 005);
        hitCounter.set("xyz", 211);
        hitCounter.set("xyz", 212);

        System.out.println(hitCounter.count("xyz", 400));
        System.out.println(hitCounter.count("abc", 400));
        System.out.println(hitCounter.count("no", 400));
    }
}
