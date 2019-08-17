package design.taskScheduler;

import java.util.concurrent.DelayQueue;

public class TaskScheduler {
    public static void main(String[] args) {
        DelayQueue<Task> queue = new DelayQueue<>();
        new Thread(new TaskConsumer(queue), "Consumer Thread").start();
        new Thread(new TaskProducer(queue), "Producer Thread").start();
    }
}
