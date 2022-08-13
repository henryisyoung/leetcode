package roblox.onsite;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class NextedTransactionHashMap {
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
    }

    // https://www.1point3acres.com/bbs/thread-909067-1-1.html
    Map<String, Value> map ;
    Stack<Map<String, Value>> snapshotVectors;
    public NextedTransactionHashMap() {
        this.map = new HashMap<>();
        this.snapshotVectors = new Stack<>();
    }

    public String get(String key) {
        if (!snapshotVectors.isEmpty()) {
            Map<String, Value> snapshotMap = snapshotVectors.peek();
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
        if (!snapshotVectors.isEmpty()) {
            Map<String, Value> snapshotMap = snapshotVectors.peek();
            snapshotMap.put(key, new Value(value, false));
        } else {
            map.put(key, new Value(value, false));
        }
    }

    public void delete(String key) {
        if (!snapshotVectors.isEmpty()) {
            Map<String, Value> snapshotMap = snapshotVectors.peek();
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
        Map<String, Value> snapshotMap = new HashMap<>();
        Map<String, Value> prevMap = snapshotVectors.isEmpty() ? map : snapshotVectors.peek();
        for (String key : prevMap.keySet()) {
            Value value = new Value(prevMap.get(key));
            snapshotMap.put(key, value);
        }
        snapshotVectors.push(snapshotMap);
    }

    public void commit() {
        Map<String, Value> snapshotMap = snapshotVectors.pop();
        if (snapshotVectors.isEmpty()) {
            for (String key : snapshotMap.keySet()) {
                Value value = snapshotMap.get(key);
                map.put(key, value);
            }
        } else {
            for (String key : snapshotMap.keySet()) {
                Value value = snapshotMap.get(key);
                snapshotVectors.peek().put(key, value);
            }
        }
    }

    public void rollback() {
        if (!snapshotVectors.isEmpty()) {
            snapshotVectors.pop();
        }
    }

    public static void main(String[] args) {
        NextedTransactionHashMap txMap = new NextedTransactionHashMap();
        txMap.set("user1", "shirt");
        txMap.set("user2", "hat");
        System.out.println(txMap.get("user1")); // shirt
        System.out.println(txMap.get("user1")); // shirt

        txMap.begin();
        System.out.println(txMap.get("user1")); // shirt
        txMap.set("user1", "pants");
        txMap.begin();
        System.out.println(txMap.get("user1")); // pants
        txMap.set("user1", "caps");
        System.out.println(txMap.get("user1")); // # caps
        txMap.delete("user2");
        System.out.println(txMap.get("user2")); // # None or Null
        txMap.rollback();//  # states return to the begin()
        System.out.println(txMap.get("user1")); // # pants
        System.out.println(txMap.get("user2")); // # hat
        txMap.commit();
        System.out.println(txMap.get("user2")); // # hat
        System.out.println(txMap.get("user1")); // # pants
    }
}
