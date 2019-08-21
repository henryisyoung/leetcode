package design;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TokenBuckets2 {
    int capacity, count, speed, tokensPerUnit;
    long lastFillTime;

    public TokenBuckets2(int maxCapacity, int tokensPerUnit) {
        this.capacity = maxCapacity;
        this.count = 0;
        this.lastFillTime = System.currentTimeMillis();
        this.speed = tokensPerUnit;
    }

    public void getTokens(int n) {
        if (n < 0 || n > capacity) {
            throw new RuntimeException("Out of capacity");
        }
        int tokens = 0;
        while (true) {
            synchronized (this) {
                refillTokens();
                if (count > 0) {
                    tokens++;
                    count--;
                    System.out.println("Thread "+Thread.currentThread().getId()+" gets 1 tokens");
                }
            }
            if (tokens == n) break;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void refillTokens() {
        long now = System.currentTimeMillis();
        int addTokens = (int) ((lastFillTime - now) / 1000 * speed);
        if (addTokens > 0) {
            count = Math.min(count + addTokens, capacity);
            lastFillTime = now;
        }
    }

    public synchronized void put(int n) {
        count = Math.min(capacity, count + n);
    }


}
