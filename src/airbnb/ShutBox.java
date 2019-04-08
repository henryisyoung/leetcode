package airbnb;

import java.util.*;

public class ShutBox {
    List<Integer> plates;
    Map<Integer, Integer> map;
    public ShutBox() {
        this.plates = new ArrayList<>();
        this.map = new HashMap<>();
        for (int i = 1; i <= 9; i++) {
            plates.add(i);
            map.put(i, i - 1);
        }
    }

    public boolean playShutBoxGame() {
        while (plates.size() > 0) {
            int sum = getDicesCombination();
            boolean result = findCombination(sum);
//            System.out.println("sum " + sum);
//            System.out.println("result " + result);
//            System.out.println("plates" + plates.toString());
//            System.out.println();
            if (result == false) {
                return false;
            }
        }
        return true;
    }

    private boolean findCombination(int sum) {
        if (map.containsKey(sum)) {
            plates.remove(Integer.valueOf(sum));
            map.remove(sum);
            return true;
        }
        int n = plates.size();
        for (int i = 0; i < n; i++) {
            int val = plates.get(i);
            int diff = sum - val;
            if (map.containsKey(diff) && diff != val) {
                map.remove(diff);
                map.remove(val);
                plates.remove(Integer.valueOf(diff));
                plates.remove(Integer.valueOf(val));
                return true;
            }
        }
        return false;
    }

    private int getDicesCombination() {
        Random random = new Random();
        int max = 6 , min = 1;
        return random.nextInt(max - min + 1) + min + random.nextInt(max - min + 1) + min;
    }

    public static void main(String[] args) {
        int win = 0;
        for (int i = 0; i < 5000; i++) {
            ShutBox solver = new ShutBox();
            boolean result = solver.playShutBoxGame();
            if (result) {
                win++;
            }
        }
        System.out.println(win * 1.0 / 5000);
    }



}
