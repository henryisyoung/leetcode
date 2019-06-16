package leetcode.dataStructrue;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class MyMap<K, V> {
    Map<K, V> valueMap;
    Map<K, Long> timeMap;
    private Thread cleanWorker = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (K k : valueMap.keySet()) {
                get(k);
            }
        }
    });
    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    public MyMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public MyMap(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    public MyMap(int capacity, float loadFactor) {
        valueMap = new ConcurrentHashMap<>(capacity, loadFactor);
        timeMap = new ConcurrentHashMap<>(capacity, loadFactor);
        cleanWorker.start();
    }

    public synchronized V get(K key) {
        Long now = System.currentTimeMillis();
        Long expired = timeMap.get(key);
        if (expired == null) return null;
        if (Long.compare(expired, now) < 0) {
            timeMap.remove(key);
            valueMap.remove(key);
            return null;
        } else {
            return valueMap.get(key);
        }
    }

    public V put(K key, V value, long duration) {
        long now = System.currentTimeMillis();
        long expired = now + duration;
        timeMap.put(key, expired);
        return valueMap.put(key, value);
    }
}

