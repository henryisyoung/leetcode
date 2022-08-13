package databricks;

import java.util.HashMap;
import java.util.Map;

public class KVStoreCounter {
    //https://www.1point3acres.com/bbs/thread-906983-1-1.html
//    设计一个mockHashMap的class，其中包含这几个API：
//    put(key, val)
//    get(key)
//    messure_put_load()
//    messure_get_load()
//    其中put和get就和普通hashmap一样，messure方法需要返回 average times per second that put/get function be called within past 5 minutes，
//    就是当前时间的‍前五分钟内，平均每秒钟put/get 被调用的次数
    Map<Integer, Integer> map;
    int[] getCounter, putCounter;
    long[] getTimer, putTimer;
    int span;
    public KVStoreCounter(int span) {
        this.map = new HashMap<>();
        this.getTimer = new long[span];
        this.getCounter = new int[span];
        this.putTimer = new long[span];
        this.putCounter = new int[span];
        this.span = span;
    }

    public void put(int key,int val) {
        map.put(key, val);
        logPutEvent();
    }

    private void logGetEvent() {
        long timeNow = getTimeNow();

        int pos = (int) (timeNow % 300);
        if (getTimer[pos] == 0) {
            getCounter[pos]++;
        } else {
            if (getTimer[pos] == timeNow) {
                getCounter[pos]++;
            } else {
                getCounter[pos] = 1;
            }
        }
        getTimer[pos] = timeNow;
    }

    public int get(int key) {
        int val = map.getOrDefault(key, -1);
        logGetEvent();
        return val;
    }

    private void logPutEvent() {
        long timeNow = getTimeNow();

        int pos = (int) (timeNow % 300);
        if (putTimer[pos] == 0) {
            putCounter[pos]++;
        } else {
            if (putTimer[pos] == timeNow) {
                putCounter[pos]++;
            } else {
                putCounter[pos] = 1;
            }
        }
        putTimer[pos] = timeNow;
    }

    public int measurePutLoad() {
        int count = 0;
        long timeNow = getTimeNow();
        for (int i = 0; i < 300; i++) {
            long prevTime = putTimer[i];
            if (prevTime + 300 >= timeNow) {
                count += putCounter[i];
            }
        }
        return count / 300;
    }

    public int measureGetLoad() {
        int count = 0;
        long timeNow = getTimeNow();
        for (int i = 0; i < 300; i++) {
            long prevTime = getTimer[i];
            if (prevTime + 300 >= timeNow) {
                count += getCounter[i];
            }
        }
        return count / 300;
    }

    private long getTimeNow() {
        return System.currentTimeMillis() / 1000;
    }

    public static void main(String[] args) throws InterruptedException {
        KVStoreCounter store = new KVStoreCounter(300);
        for (int i = 0; i < 100; i++) {
            Thread.sleep(20);
            if (i % 2 == 0) {
                store.put(i, i);
            } else {
                System.out.println(store.get(i - 1));
            }
        }
        System.out.println(store.measureGetLoad());
        System.out.println(store.measurePutLoad());
    }
}
