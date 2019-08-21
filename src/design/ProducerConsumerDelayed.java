package design;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static sun.security.krb5.internal.LoginOptions.MAX;

public class ProducerConsumerDelayed {
    static class TimerTask implements Delayed{
        long start;
        public TimerTask(int delay) {
            this.start = System.currentTimeMillis() + delay;
        }
        @Override
        public long getDelay(TimeUnit unit) {
            long diff = start - System.currentTimeMillis();
            return unit.convert(diff, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return (int) ((this.start - ((TimerTask) o).start) % 10);
        }
    }

    public static void main(String[] args) {
        DelayQueue<TimerTask> queue = new DelayQueue<>();
        beginConsumer(queue);
        beginProducer(queue);
    }

    private static void beginConsumer(DelayQueue<TimerTask> queue) {
       for (int i = 0; i < 10; i++) {
           new Thread(new Runnable() {
               @Override
               public void run() {
                   while (true) {
                       try {
                           TimerTask t = queue.take();
                           System.out.println("Take " + t.toString());
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }

                   }
               }
           },"c-" + i).start();
       }
    }

    private static void beginProducer(DelayQueue<TimerTask> queue) {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        TimerTask t = new TimerTask(10);
                        queue.put(t);
                        System.out.println("Put " + t.toString());
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            },"p-" + i).start();
        }
    }

}
