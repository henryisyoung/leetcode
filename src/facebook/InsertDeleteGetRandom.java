package facebook;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InsertDeleteGetRandom {
    Map<Integer, Integer> map1, map2;
    Random random;
    /** Initialize your data structure here. */
    public InsertDeleteGetRandom() {
        this.map1 = new HashMap<>();
        this.map2 = new HashMap<>();
        this.random = new Random();
    }

    /** Inserts a value to the set. Returns true if the set did not already contain the specified element. */
    public boolean insert(int val) {
        if (map1.containsKey(val)) return false;
        int index = map1.size();
        map1.put(val, index);
        map2.put(index, val);
        return true;
    }

    /** Removes a value from the set. Returns true if the set contained the specified element. */
    public boolean remove(int val) {
        if (!map1.containsKey(val)) return false;
        int pos = map1.get(val);
        map1.remove(val);
        map2.remove(pos);
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
        return map2.get(random.nextInt(map2.size()));
    }
}
