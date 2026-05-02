package snowflake.mianjing.selfreview;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentKVStore {
    static class TxState {
        int val;
        boolean isDeleted;

        public TxState(int val, boolean isDeleted) {
            this.val = val;
            this.isDeleted = isDeleted;
        }

        public TxState(int val) {
            this.val = val;
            this.isDeleted = false;
        }
    }

    Map<Integer, Integer> store;
    ThreadLocal<Map<Integer, TxState>> tx;
    ReentrantLock commitLock;

    public ConcurrentKVStore() {
        this.store = new ConcurrentHashMap<>();
        this.tx = new ThreadLocal<>();
        this.commitLock = new ReentrantLock();
    }

    public Integer get(int key) {
        Map<Integer, TxState> myTx = tx.get();
        if (myTx != null && myTx.containsKey(key)) {
            TxState state = myTx.get(key);
            if (state.isDeleted) return null;
            return state.val;
        }
        return store.get(key);
    }

    public void add(int key, int val) {
        Map<Integer, TxState> myTx = tx.get();
        if (myTx != null) {
            myTx.put(key, new TxState(val));
        } else {
            store.put(key, val);
        }
    }

    public void delete(int key) {
        Map<Integer, TxState> myTx = tx.get();
        if (myTx != null) {
            if (myTx.containsKey(key)) {
                TxState state = myTx.get(key);
                state.isDeleted = true;
                myTx.put(key, state);
            } else {
                myTx.put(key, new TxState(0, true));
            }
        } else {
            store.remove(key);
        }
    }

    public void begin() {
        if (tx.get() != null) {
            throw new IllegalStateException("Tx is started");
        }
        tx.set(new HashMap<>());
    }

    public void commit() {
        Map<Integer, TxState> myTx = tx.get();
        if (myTx == null) {
            throw new IllegalStateException("No tx is started");
        }
        try {
            commitLock.lock();
            try {
                for (Map.Entry<Integer, TxState> e : myTx.entrySet()) {
                    int key = e.getKey();
                    TxState state = e.getValue();
                    if (state.isDeleted) {
                        store.remove(key);
                    } else {
                        store.put(key, state.val);
                    }
                }
            } finally {
                commitLock.unlock();
            }
        } finally {
            tx.remove();
        }
    }

    public void revert() {
        if (tx.get() == null) {
            throw new IllegalStateException("No tx is started");
        }
        tx.remove();
    }
}
