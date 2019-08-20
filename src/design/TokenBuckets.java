package design;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TokenBuckets {
    private Queue<String> queue;
    private Lock lock = new ReentrantLock();
    private Condition producer, consumer;
    private int MAX, conut = 0, span;

    public TokenBuckets(int n, int span) {
        this.MAX = n;
        this.span = span;
        this.queue = new LinkedList<>();
        this.producer = lock.newCondition();
        this.consumer = lock.newCondition();
    }

    public void put(String str) {
        try {
            lock.lock();
            while (queue.size() == MAX) {
                producer.await();
            }
            conut++;
            queue.add(str);
            consumer.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public String get(int n) {
        String result = null;
        try {
            while (n > 0) {
                n--;
                lock.lock();
                while (queue.isEmpty()) {
                    consumer.await();
                }
                conut--;
                result = queue.poll();
                producer.signalAll();;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return result;
    }

    private void beginConsumer() {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        String cur = get(2);
                        System.out.println("GET value " + cur);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, "consumer-" + i).start();
        }
    }

    private void beginProducer() {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        String now = String.valueOf(System.currentTimeMillis());
                        put(now);
                        System.out.println("PUT value " + now);
                        try {
                            Thread.sleep(span);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, "producer-" + i).start();
        }
    }

    public static void main(String[] args) {
        TokenBuckets worker = new TokenBuckets(10, 1000);
        worker.beginConsumer();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        worker.beginProducer();
    }
}
