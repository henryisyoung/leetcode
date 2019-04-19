package airbnb;

import java.util.*;

public class ShutBox {
    Set<Integer> set;
    public ShutBox() {
        this.set = new HashSet<>();
        for (int i = 1; i <= 9; i++) {
            set.add(i);
        }
    }

    public boolean playShutBoxGame() {
        while (!set.isEmpty()) {
            int randomDice = getRandomDices();
            boolean result = findResult(randomDice);
            if (!result) {
                return false;
            }
        }
        return true;
    }

    private boolean findResult(int val) {
        if (set.contains(val)) {
            set.remove(val);
            return true;
        }
        for (int cur : set) {
            int diff = val - cur;
            if (set.contains(diff) && diff != cur) {
                set.remove(diff);
                set.remove(cur);
                return true;
            }
        }
        return false;
    }

    private int getRandomDices() {
        Random random = new Random();
        return random.nextInt( 6) + 1 + random.nextInt(6) + 1;
    }

    public static double calWinningStats(int n) {
        int count = 0;
        for (int i = 0; i < n; i++) {
            ShutBox sovler = new ShutBox();
            if (sovler.playShutBoxGame()) {
                count++;
            }
        }
        return (1.0 * count) / n;
    }

    public static void main(String[] args) {
        System.out.println(calWinningStats(10000));
    }
}
