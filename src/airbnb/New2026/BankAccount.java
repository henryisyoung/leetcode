package airbnb.New2026;

import java.util.ArrayList;
import java.util.List;

/**
 * Bank account: deposit / withdraw / balance(t) / transactions(start, end).
 * deposit(t, amt)      append +amt
 * withdraw(t, amt)     append -amt; returns false (no-op) on overdraft
 * balance(t)           running balance as of time t          O(log n)
 * transactions(s, e)   entries with s <= ts <= e, inclusive  O(log n + k)
 *
 * Append-only entries with a parallel running-balance list (same length) so
 * the two range queries beat a linear scan. Timestamps must be non-decreasing.
 */
public class BankAccount {
    static class Entry {
        long timestamp;
        long amount;

        public Entry (long timestamp, long amount) {
            this.timestamp = timestamp;
            this.amount = amount;
        }
    }

    List<Entry> entries;
    List<Long> balances;        // same length as entries; balances.get(i) = balance after entry i

    public BankAccount() {
        this.balances = new ArrayList<>();
        this.entries = new ArrayList<>();
    }

    public void deposit(long timeStamp, long amount) {
        appendEntry(timeStamp, amount);
    }

    public boolean withdraw(long timeStamp, long amount) {
        if (currentBalance() < amount) {
            return false;
        }
        appendEntry(timeStamp, -amount);
        return true;
    }

    private void appendEntry(long timeStamp, long amount) {
        if (!entries.isEmpty() && timeStamp < entries.getLast().timestamp) {
            throw new IllegalArgumentException("timestamps must be non-decreasing");
        }
        long prev = balances.isEmpty() ? 0 : balances.getLast();
        entries.add(new Entry(timeStamp, amount));
        balances.add(prev + amount);
    }

    public long currentBalance() {
        return balances.isEmpty() ? 0 : balances.getLast();
    }

    public long balance(long t) {
        int i = findFloor(t);                 // last entry with ts <= t
        return i < 0 ? 0 : balances.get(i);
    }

    public List<Entry> transactions(long start, long end) {
        if (start > end) {
            return new ArrayList<>();
        }
        int lo = findCeil(start);             // first entry with ts >= start
        int hi = findFloor(end);              // last  entry with ts <= end
        if (lo > hi) {                        // covers hi == -1 and lo == size
            return new ArrayList<>();
        }
        return new ArrayList<>(entries.subList(lo, hi + 1));
    }

    // largest index with entries[i].timestamp <= t, or -1 if none
    private int findFloor(long t) {
        if (entries.isEmpty() || entries.get(0).timestamp > t) {
            return -1;
        }
        int left = 0, right = entries.size() - 1;
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (entries.get(mid).timestamp <= t) {
                left = mid;
            } else {
                right = mid;
            }
        }
        return entries.get(right).timestamp <= t ? right : left;
    }

    // smallest index with entries[i].timestamp >= t, or entries.size() if none
    private int findCeil(long t) {
        int n = entries.size();
        if (n == 0 || entries.getLast().timestamp < t) {
            return n;
        }
        int left = 0, right = n - 1;
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (entries.get(mid).timestamp >= t) {
                right = mid;
            } else {
                left = mid;
            }
        }
        return entries.get(left).timestamp >= t ? left : right;
    }
}
