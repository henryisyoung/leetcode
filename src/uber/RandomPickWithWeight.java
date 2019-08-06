package uber;

import java.util.*;

public class RandomPickWithWeight {
    int max;
    Random random;
    TreeMap<Integer, Integer> map;
    public RandomPickWithWeight(int[] w) {
        int sum = 0;
        this.map = new TreeMap<>();
        for (int i = 0; i < w.length; i++) {
            map.put(sum, i);
            sum += w[i];
        }
        max = sum;
        this.random = new Random();
    }

    public int pickIndex() {
        int index = random.nextInt(max);
        return map.floorEntry(index).getValue();
    }
}
