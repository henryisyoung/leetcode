package design;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumer {
    private Queue<String> queue;
    private Lock lock = new ReentrantLock();
    private Condition producer, consumer;
    private int MAX = 10, conut = 0;

    public ProducerConsumer() {
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

    public String get() {
        String result = null;
        try {
            lock.lock();
            while (queue.isEmpty()) {
                consumer.await();
            }
            conut--;
            result = queue.poll();
            producer.signalAll();;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return result;
    }

    public static void main(String[] args) {
        ProducerConsumer worker = new ProducerConsumer();
        beginConsumer(worker);
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        beginProducer(worker);
    }

    private static void beginConsumer(ProducerConsumer worker) {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        String cur = worker.get();
                        System.out.println("GET value " + cur);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, "consumer-" + i).start();
        }
    }

    private static void beginProducer(ProducerConsumer worker) {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        String now = String.valueOf(System.currentTimeMillis());
                        worker.put(now);
                        System.out.println("PUT value " + now);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, "producer-" + i).start();
        }
    }

}
