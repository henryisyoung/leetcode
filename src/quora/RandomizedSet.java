package quora;

import java.util.List;
import java.util.*;

public class RandomizedSet {
    Map<Integer, Integer> map1, map2;
    /** Initialize your data structure here. */
    public RandomizedSet() {
        this.map1 = new HashMap<>();
        this.map2 = new HashMap<>();
    }

    /** Inserts a value to the set. Returns true if the set did not already contain the specified element. */
    public boolean insert(int val) {
        if (map1.containsKey(val)) {
            return false;
        }
        map1.put(val, map2.size());
        map2.put(map2.size(), val);
        return true;
    }

    /** Removes a value from the set. Returns true if the set contained the specified element. */
    public boolean remove(int val) {
        if (!map1.containsKey(val)) {
            return false;
        }
        int pos = map1.get(val);
        map2.remove(pos);
        map1.remove(val);
        if (pos == map2.size()) {
            return true;
        }
        int key = map2.get(map2.size());
        map1.put(key, pos);
        map2.remove(map2.size());
        map2.put(pos, key);
        return true;
    }

    /** Get a random element from the set. */
    public int getRandom() {
        if(map1.size()==0){
            return -1;
        }
        Random rand = new Random();
        int pos = rand.nextInt(map2.size());
        return map2.get(pos);
    }
}
