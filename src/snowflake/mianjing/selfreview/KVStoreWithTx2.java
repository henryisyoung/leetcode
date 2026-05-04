package snowflake.mianjing.selfreview;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class KVStoreWithTx2 {
    static class TxState {
        int val;
        boolean isDeleted;

        public TxState(int val) {
            this.val = val;
            this.isDeleted = false;
        }

        public TxState(int val, boolean isDeleted) {
            this.val = val;
            this.isDeleted = isDeleted;
        }
    }

    Map<Integer, Integer> map;
    ThreadLocal<Map<Integer, TxState>> tnxSet;
    ReentrantLock lock;

    public KVStoreWithTx2(){
        this.map = new ConcurrentHashMap<Integer, Integer>() ;
        this.lock = new ReentrantLock();
        tnxSet = new ThreadLocal<>();
    }

    public Integer get(int key) {
        Map<Integer, TxState> tnx = tnxSet.get();
        if (tnx != null && tnx.containsKey(key)) {
            TxState state = tnx.get(key);
            return state.isDeleted ? null : state.val;
        }

        return map.get(key);
    }

    public void add(int key, int val) {
        Map<Integer, TxState> tnx = tnxSet.get();

        if (tnx != null) {
            tnx.put(key, new TxState(val));
            return;
        }

        map.put(key, val);
    }

    public void delete(int key) {
        Map<Integer, TxState> tnx = tnxSet.get();

        if (tnx != null) {
            if (tnx.containsKey(key)) {
                TxState state = tnx.get(key);
                state.isDeleted = true;
            } else {
                TxState state = new TxState(0, true);
                tnx.put(key, state);
            }
        } else {
            map.remove(key);
        }
    }

    public void begin() {
        Map<Integer, TxState> tnx = tnxSet.get();

        if (tnx != null) {
            throw new IllegalStateException("Already started a tx");
        }

        tnxSet.set(new HashMap<>());
    }

    public void commit() {
        Map<Integer, TxState> tnx = tnxSet.get();

        if (tnx == null) {
            throw new IllegalStateException("No active tnx");
        }

        try {
            try {
                lock.lock();

                for (Map.Entry<Integer, TxState> entry : tnx.entrySet()) {
                    int key = entry.getKey();
                    TxState state = entry.getValue();

                    if (state.isDeleted) {
                        map.remove(key);
                    } else {
                        map.put(key, state.val);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        finally {
            tnxSet.remove();
        }
    }

    public void revert() {
        Map<Integer, TxState> tnx = tnxSet.get();

        if (tnx == null) {
            throw new IllegalStateException("No active tnx");
        }

        tnxSet.remove();
    }

    public static void main(String[] arg) {
        KVStoreWithTx2 store = new KVStoreWithTx2();

        store.add(1, 11);
        store.add(2, 22);

        System.out.println(store.get(2) == 22);

        // tx - revert
        store.begin();
        store.add(2, 55);
        System.out.println(store.get(2) == 55);
        store.revert();
        System.out.println(store.get(2) == 22);

        // commit
        store.begin();
        store.add(2, 55);
        System.out.println(store.get(2) == 55);
        store.commit();
        System.out.println(store.get(2) == 55);

        // delete
        store.begin();
        store.delete(2);
        System.out.println(store.get(2) == null);
        store.commit();
        System.out.println(store.get(2) == null);
    }
}
