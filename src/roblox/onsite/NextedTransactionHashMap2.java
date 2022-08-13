package roblox.onsite;

import java.util.*;

public class NextedTransactionHashMap2 {
    // https://www.1point3acres.com/bbs/thread-909067-1-1.html
    // https://leetcode.com/discuss/interview-question/279913/bloomberg-onsite-key-value-store-with-transactions
    Map<String, String> map ;
    List<Map<String, String>> snapshotVectors;
    List<Set<String>> deletedVectors;
    public NextedTransactionHashMap2() {
        this.map = new HashMap<>();
        this.snapshotVectors = new ArrayList<>();
        snapshotVectors.add(map);
        this.deletedVectors = new ArrayList<>();
        deletedVectors.add(new HashSet<>());
    }

    public String get(String key) {
        for (int i = snapshotVectors.size() - 1; i >= 0; i--) {
            Map<String, String> curMap = snapshotVectors.get(i);
            Set<String> deleted = deletedVectors.get(i);
            if (deleted.contains(key)) {
                return null;
            } else if (curMap.containsKey(key)) {
                return curMap.get(key);
            }
        }
        return null;
    }

    public void set(String key, String value) {
        int size = snapshotVectors.size();
        Map<String, String> curMap = snapshotVectors.get(size - 1);
        Set<String> deleted = deletedVectors.get(size - 1);
        curMap.put(key, value);
        deleted.remove(key);
    }

    public void delete(String key) {
        int size = snapshotVectors.size();
        Map<String, String> curMap = snapshotVectors.get(size - 1);
        Set<String> deleted = deletedVectors.get(size - 1);
        curMap.remove(key);
        deleted.add(key);
    }

    public void begin() {
        snapshotVectors.add(new HashMap<>());
        deletedVectors.add(new HashSet<>());
    }

    public void commit() {
        int size = snapshotVectors.size();
        Map<String, String> curMap = snapshotVectors.get(size - 1);
        Set<String> deleted = deletedVectors.get(size - 1);
        snapshotVectors.remove(size - 1);
        deletedVectors.remove(size - 1);
        size = snapshotVectors.size();
        Map<String, String> prevMap = snapshotVectors.get(size - 1);
        Set<String> prevDeleted = deletedVectors.get(size - 1);
        for (String key : curMap.keySet()) {
            prevMap.put(key, curMap.get(key));
            prevDeleted.remove(key);
        }
        for (String key : deleted) {
            prevMap.remove(key);
        }
        prevDeleted.addAll(deleted);
    }

    public void rollback() {
        int size = snapshotVectors.size();
        snapshotVectors.remove(size - 1);
        deletedVectors.remove(size - 1);
    }

    public static void main(String[] args) {
        NextedTransactionHashMap2 txMap = new NextedTransactionHashMap2();
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

        txMap.begin();
        System.out.println(txMap.get("user1")); // pants
        txMap.set("user1", "caps");
        System.out.println(txMap.get("user1")); // # caps
        txMap.delete("user2");
        System.out.println(txMap.get("user2")); // # None or Null
        txMap.commit();//  # states return to the begin()


        txMap.commit();
        System.out.println(txMap.get("user2")); // # null
        System.out.println(txMap.get("user1")); // # caps
    }
}
