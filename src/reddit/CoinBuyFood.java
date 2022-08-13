package reddit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CoinBuyFood {
    static int max = 0;
    static List<String> maxList;
    public static List<List<String>> buyFood(int coins, List<Food> food) {
        List<List<String>> result = new ArrayList<>();
        if (food == null || food.size() == 0 || coins <= 0) {
            return result;
        }
        Collections.sort(food, (a, b) -> (a.val - b.val));
        List<String> list = new ArrayList<>();
        maxList = new ArrayList<>();
        dfsFindAll(list, result, 0, coins, 0, food);
        System.out.println("most expensive list: " + maxList);
        return result;
    }

    private static void dfsFindAll(List<String> list, List<List<String>> result, int cur, int coins, int pos, List<Food> food) {
        if (cur > coins) return;
        result.add(new ArrayList<>(list));
        if (cur > max) {
            max = cur;
            maxList = new ArrayList<>(list);
        }
        if (pos == food.size()) {
            return;
        }
        for (int i = pos; i < food.size(); i++) {
            if (cur + food.get(i).val > coins) break;
            list.add(food.get(i).name);
            dfsFindAll(list, result, cur + food.get(i).val, coins, i + 1, food);
            list.remove(list.size() - 1);
        }
    }

    public static List<List<String>> buyFood2(int coins, List<Food> food) {
        List<List<String>> result = new ArrayList<>();
        if (food == null || food.size() == 0 || coins <= 0) {
            return result;
        }
        Collections.sort(food, (a, b) -> (a.val - b.val));
        List<String> list = new ArrayList<>();
        dfsFindAll2(list, result, 0, coins, 0, food);
        return result;
    }

    public static List<List<String>> buyFavFood(int coins, List<Food> food, Food fav) {
        List<List<String>> result = new ArrayList<>();
        if (food == null || food.size() == 0 || coins <= 0) {
            return result;
        }
        Collections.sort(food, (a, b) -> (a.val - b.val));
        List<String> list = new ArrayList<>();
        list.add(fav.name);
        dfsFindAll2(list, result, fav.val, coins, 0, food);
        return result;
    }

    private static void dfsFindAll2(List<String> list, List<List<String>> result, int cur, int coins, int pos, List<Food> food) {
        result.add(new ArrayList<>(list));
        if (pos == food.size()) {
            return;
        }
        for (int i = pos; i < food.size(); i++) {
            if (cur + food.get(i).val > coins) break;
            list.add(food.get(i).name);
            dfsFindAll2(list, result, cur + food.get(i).val, coins, i, food);
            list.remove(list.size() - 1);
        }
    }

    static class Food{
        String name;
        int val;
        public Food(int val, String name) {
            this.name = name;
            this.val = val;
        }
    }

    public static void main(String[] args) {
        int coins = 800;
        List<Food> foods = Arrays.asList(
                new Food(200, "apple"),
                new Food(300, "banana"),
//                new Food(200, "beer"),
                new Food(400, "orange")
        );
//        System.out.println(buyFood(coins, foods));
        System.out.println(buyFood2(coins, foods));
//        System.out.println(buyFavFood(coins, foods, new Food(200, "apple")));
    }
}
