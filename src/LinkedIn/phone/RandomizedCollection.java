package LinkedIn.phone;

import java.util.*;

public class RandomizedCollection {
    Map<Integer, Set<Integer>> keyIndexes;
    List<Integer> numbers;
    Random random;

    public RandomizedCollection() {
        this.keyIndexes = new HashMap<>();
        this.numbers = new ArrayList<>();
        this.random = new Random();
    }

    public boolean insert(int val) {
        keyIndexes.putIfAbsent(val, new HashSet<>());
        keyIndexes.get(val).add(numbers.size());

        numbers.add(val);
        return keyIndexes.get(val).size() == 1;
    }

    public boolean remove(int val) {
        if (!keyIndexes.containsKey(val) || keyIndexes.get(val).isEmpty()) {
            return false;
        }

        int removeIndex = keyIndexes.get(val).iterator().next();
        keyIndexes.get(val).remove(removeIndex);

        int lastNumber = numbers.getLast();
        numbers.set(removeIndex, lastNumber);

        keyIndexes.get(lastNumber).add(removeIndex);
        keyIndexes.get(lastNumber).remove(numbers.size() - 1);
        numbers.removeLast();
        return true;
    }

    public int getRandom() {
        int index = random.nextInt(numbers.size());
        return numbers.get(index);
    }
}
