package snowflake.mianjing.selfreview;

import java.util.HashMap;
import java.util.Map;

public class KVStoreWithTx {
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
    Map<Integer, TxState> tx;

    public KVStoreWithTx(){
        this.store = new HashMap<>();
    }

    public Integer get(int key) {
        if (tx != null && tx.containsKey(key)) {
            TxState state = tx.get(key);
            if (state.isDeleted) return null;
            return state.val;
        }
        return store.get(key);
    }

    public void add(int key, int val) {
        if (tx != null) {
            tx.put(key, new TxState(val));
        } else {
            store.put(key, val);
        }
    }

    public void delete(int key) {
        if (tx != null) {
            if (tx.containsKey(key)) {
                TxState state = tx.get(key);
                state.isDeleted = true;
                tx.put(key, state);
            } else {
                tx.put(key, new TxState(0, true));
            }
        } else {
            store.remove(key);
        }
    }

    public void begin() {
        if (tx != null) {
            throw new IllegalStateException("Tx is started");
        }
        tx = new HashMap<>();
    }

    public void commit() {
        if (tx == null) {
            throw new IllegalStateException("No tx is started");
        }
        for (Map.Entry<Integer, TxState> e : tx.entrySet()) {
            int key = e.getKey();
            TxState state = e.getValue();
            if (state.isDeleted) {
                store.remove(key);
            } else {
                store.put(key, state.val);
            }
        }

        tx = null;
    }

    public void revert() {
        if (tx == null) {
            throw new IllegalStateException("No tx is started");
        }
        tx = null;
    }
}
