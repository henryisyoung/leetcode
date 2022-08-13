package roblox.onsite;

import java.util.HashMap;
import java.util.Map;

public class TransactionHashMap {
    class Value {
        String value;
        boolean deleted;
        public Value() {
            this.value = "";
            this.deleted = false;
        }

        public Value(String value, boolean deleted) {
            this.value = value;
            this.deleted = deleted;
        }

        public Value(Value value) {
            this.value = value.value;
            this.deleted = value.deleted;
        }

        @Override
        public String toString() {
            return "Value{" +
                    "value='" + value + '\'' +
                    ", deleted=" + deleted +
                    '}';
        }
    }

    // https://www.1point3acres.com/bbs/thread-909067-1-1.html
    Map<String, Value> map ;
    Map<String, Value> snapshotMap;
    boolean inTx;
    public TransactionHashMap() {
        this.map = new HashMap<>();
        this.snapshotMap = new HashMap<>();
        this.inTx = false;
    }

    public String get(String key) {
        if (inTx) {
            if (snapshotMap.containsKey(key) && !snapshotMap.get(key).deleted) {
                return snapshotMap.get(key).value;
            }
            return null;
        } else {
            if (map.containsKey(key) && !map.get(key).deleted) {
                return map.get(key).value;
            }
            return null;
        }
    }

    public void set(String key, String value) {
        if (inTx) {
            snapshotMap.put(key, new Value(value, false));
        } else {
            map.put(key, new Value(value, false));
        }
    }

    public void delete(String key) {
        if (inTx) {
            if (snapshotMap.containsKey(key)) {
                snapshotMap.get(key).deleted = true;
            }
        } else {
            if (map.containsKey(key)) {
                map.get(key).deleted = true;
            }
        }
    }

    public void begin() {
        inTx = true;
        snapshotMap.clear();
        for (String key : map.keySet()) {
            Value value = new Value(map.get(key));
            snapshotMap.put(key, value);
        }
    }

    public void commit() {
        inTx = false;
        for (String key : snapshotMap.keySet()) {
            Value value = snapshotMap.get(key);
            map.put(key, value);
        }
    }

    public void rollback() {
        inTx = false;
        snapshotMap.clear();
    }

    public static void main(String[] args) {
//        TransactionHashMap txMap = new TransactionHashMap();
//        txMap.set("user1", "shirt");
//        txMap.set("user2", "hat");
//        System.out.println(txMap.get("user1")); // shirt
//        txMap.begin();
//        System.out.println(txMap.get("user1")); // shirt
//
//        txMap.set("user1", "pants");
//        System.out.println(txMap.get("user1")); // # pants
//        txMap.delete("user2");
//        System.out.println(txMap.get("user2")); // # None or Null
//        txMap.rollback();//  # states return to the begin()
//        System.out.println(txMap.get("user1")); // # shirt
//        System.out.println(txMap.get("user2")); // # hat

        TransactionHashMap txMap = new TransactionHashMap();
        txMap.set("user1", "shirt");
        txMap.set("user2", "hat");
        System.out.println(txMap.get("user1")); // shirt
        System.out.println(txMap.get("user1")); // shirt
        txMap.begin();
        System.out.println(txMap.get("user1")); // shirt

        txMap.set("user1", "pants");
        System.out.println(txMap.get("user1")); // # pants
        txMap.delete("user2");
        System.out.println(txMap.get("user2")); // # None or Null
        txMap.commit();//  # states return to the begin()
        System.out.println(txMap.get("user1")); // # pants
        System.out.println(txMap.get("user2")); // # null
    }
}
