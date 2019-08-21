package design;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TokenBuckets3 {
    long lastFillTime;
    Lock lock;
    Condition producer, customer;
    int maxCapacity, count, tokensPerUnit;
    Queue<Integer> tokens;
    public TokenBuckets3(int maxCapacity, int tokensPerUnit) {
        this.lock = new ReentrantLock();
        this.customer = lock.newCondition();
        this.producer = lock.newCondition();
        this.count = 0;
        this.lastFillTime = System.currentTimeMillis();
        this.maxCapacity = maxCapacity;
        this.tokensPerUnit = tokensPerUnit;
        this.tokens = new LinkedList<>();
    }

    public List<Integer> getTokens(int n) {
        if(n <= 0)
            throw new IllegalArgumentException("Cannot get zero or negative number of tokens.");
        if(n > maxCapacity)
            throw new IllegalArgumentException("Cannot get tokens more than max capacity.");
        List<Integer> result = new ArrayList<>();
        try {
            while (n > 0) {
                lock.lock();
                while (tokens.size() == 0) {
                    customer.await();
                }
                n--;
                count--;
                result.add(tokens.poll());
                producer.signalAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return result;
    }

    public void put() {
        try {
            lock.lock();
            while (tokens.size() == maxCapacity) {
                producer.await();
            }
            long cur = System.currentTimeMillis();
            int addTokens = (int) Math.min(maxCapacity - count, (cur - lastFillTime) * tokensPerUnit);
            lastFillTime = cur;
            count += addTokens;
            for (int i = 0; i < addTokens; i++) tokens.add(i);
            customer.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
