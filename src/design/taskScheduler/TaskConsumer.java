package design.taskScheduler;

import java.util.concurrent.DelayQueue;

public class TaskConsumer implements Runnable {
    private DelayQueue<Task> q;

    public TaskConsumer(DelayQueue<Task> q) {
        this.q = q;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Task task = q.take();
                System.out.println("Take " + task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

