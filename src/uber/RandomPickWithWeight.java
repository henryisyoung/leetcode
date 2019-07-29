package uber;

import java.util.*;

public class RandomPickWithWeight {
    private TreeMap<Integer, Integer> map;
    private int max;
    private Random rd;

    public RandomPickWithWeight(int[] w) {
        this.map = new TreeMap<>();
        int sum = 0;
        for(int i = 0; i < w.length; i++) {
            map.put(sum, i);
            sum += w[i];
        }

        this.max = sum;
        this.rd = new Random();
    }

    public int pickIndex() {
        int r = rd.nextInt(max);
        return map.floorEntry(r).getValue();
    }
}
