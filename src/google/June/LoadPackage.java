package google.June;

import java.util.ArrayList;
import java.util.List;

public class LoadPackage {
    public static List<Integer> loadPackage(int[] bags, int limit) {
        List<Integer> result = new ArrayList<>();
        if (bags == null || bags.length == 0 || limit <= 0) return result;

        List<List<Integer>> pkgs = new ArrayList<>();

        int capacity = limit;

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < bags.length; i++) {
            if (bags[i] > limit) continue;
            if (capacity > bags[i]) {
                list.add(bags[i]);
                capacity -= bags[i];
            } else {
                capacity = limit;
                pkgs.add(new ArrayList<>(list));
                list = new ArrayList<>();
                list.add(bags[i]);
                capacity -= bags[i];
            }
        }
        pkgs.add(new ArrayList<>(list));

        for (int i = pkgs.size() - 1; i >= 0; i--) {
            for (int bag : pkgs.get(i)) {
                result.add(bag);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(loadPackage(new int[]{30,5,6}, 40));
        System.out.println(loadPackage(new int[]{30,5,6}, 20));
        System.out.println(loadPackage(new int[]{30,5,6}, 10));
    }
}
