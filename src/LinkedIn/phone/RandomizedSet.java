package LinkedIn.phone;

import java.util.*;

public class RandomizedSet {
    Map<Integer, Integer> indexToVal, valToIndex;
    Random random;

    public RandomizedSet() {
        this.valToIndex = new HashMap<>();
        this.indexToVal = new HashMap<>();
        this.random = new Random();
    }

    public boolean insert(int val) {
        if (valToIndex.containsKey(val)) {
            return false;
        }
        int index = indexToVal.size();
        valToIndex.put(val, index);
        indexToVal.put(index, val);
        return true;
    }

    public boolean remove(int val) {
        if (!valToIndex.containsKey(val)) {
            return false;
        }
        int removeIndex = valToIndex.get(val);
        if (removeIndex == valToIndex.size() - 1) {
            indexToVal.remove(removeIndex);
            valToIndex.remove(val);
        } else {
            int lastIndex = valToIndex.size() - 1;
            int lastVal = indexToVal.get(lastIndex);

            valToIndex.put(lastVal, removeIndex);
            indexToVal.put(removeIndex, lastVal);

            valToIndex.remove(val);
            indexToVal.remove(lastIndex);
        }

        return true;
    }

    public int getRandom() {
        int index = random.nextInt(valToIndex.size());
        return indexToVal.get(index);
    }
}
