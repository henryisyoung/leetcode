package pinterest;

import java.util.*;
import java.util.concurrent.*;

public class HitCounter {
    Map<Integer, Node> map;
    /** Initialize your data structure here. */
    public HitCounter() {
       this.map = new ConcurrentHashMap<>();
        for (int i = 0; i < 300; i++) {
            map.put(i, new Node(0, 0));
        }
    }

    public synchronized void hit() {
        //System.out.println("Thread hits " + Thread.currentThread().getName());
        long curTime = System.currentTimeMillis() / 1000;
        int index = (int) (curTime % 300);
        if (map.get(index).timestamp == curTime) {
            map.get(index).count++;
        } else {
            map.get(index).count = 1;
            map.get(index).timestamp = curTime;
        }
    }

    public synchronized int getHits() {
        long curTime = System.currentTimeMillis() / 1000;
        int count = 0;
        for (int i = 0; i < 300; i++) {
            if (map.get(i).timestamp + 300 > curTime) {
                count += map.get(i).count;
            }
        }
        return count;
    }

    class Node{
        int count;
        long timestamp;
        public Node(int count, long timestamp) {
            this.count = count;
            this.timestamp = timestamp;
        }
    }

    public static void main(String[] args) {
        HitCounter hitter = new HitCounter();
        for (int t = 0; t < 10; t++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            hitter.hit();
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, "thread-" + t).start();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println("now we have hits " + hitter.getHits());
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "thread-get" ).start();
    }
}
