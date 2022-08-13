package databricks;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class KVStore {
//    设计一个mockHashMap的class，其中包含这几个API：
//    put(key, val)
//    get(key)
//    messure_put_load()
//    messure_get_load()
//    其中put和get就和普通hashmap一样，messure方法需要返回 average times per second that put/get function be called within past 5 minutes，
//    就是当前时间的‍前五分钟内，平均每秒钟put/get 被调用的次数
    Map<Integer, Integer> map;
    TreeMap<Long, Integer> putCountMap, getCountMap;
    public KVStore() {
        this.map = new HashMap<>();
        this.getCountMap = new TreeMap<>();
        this.putCountMap = new TreeMap<>();
    }


    public void put(int key,int val) {
        map.put(key, val);
        long timeNow = getTimeNow();
        putCountMap.put(timeNow, putCountMap.getOrDefault(timeNow, 0) + 1);
    }

    public int get(int key) {
        int val = map.getOrDefault(key, -1);
        long timeNow = getTimeNow();
        getCountMap.put(timeNow, getCountMap.getOrDefault(timeNow, 0) + 1);
        return val;
    }

    public int measurePutLoad() {
        long timeNow = getTimeNow();
        long fromKey = timeNow - (5 * 60 * 1000);
        int count = 0;
        for (int val : putCountMap.tailMap(fromKey).values()) {
            count += val;
        }
        return count;
    }

    public int measureGetLoad() {
        long timeNow = getTimeNow();
        long fromKey = timeNow - (5 * 60 * 1000);
        int count = 0;
        for (int val : getCountMap.tailMap(fromKey).values()) {
            count += val;
        }
        return count;
    }

    private long getTimeNow() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) throws InterruptedException {
        KVStore store = new KVStore();
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
