package reddit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CoinBuyFood2 {
    static int max;
    static List<String> maxList;
    public static List<List<String>> buyFood(int coins, List<Food> food) {
        List<List<String>> result = new ArrayList<>();
        if (food.isEmpty()) {
            return result;
        }
        max = 0;
        maxList = new ArrayList<>();
        Collections.sort(food, (f1, f2) -> (f1.price - f2.price));
        List<String> list = new ArrayList<>();
        int pos = 0, sum = 0;

        dfsFindAll(coins, pos, sum, food, result, list);
        System.out.println(maxList);
        return result;
    }

    private static void dfsFindAll(int coins, int pos, int sum, List<Food> food,
                                   List<List<String>> result, List<String> list) {
        result.add(new ArrayList<>(list));
        if (sum > max) {
            max = sum;
            maxList = new ArrayList<>(list);
        }
        for (int i = pos; i < food.size(); i++) {
            if (sum + food.get(i).price > coins) break;
            list.add(food.get(i).name);
            dfsFindAll(coins, i + 1, sum + food.get(i).price, food, result, list);
            list.remove(list.size() - 1);
        }
    }

    public static List<List<String>> buyFoodMulti(int coins, List<Food> food) {
        List<List<String>> result = new ArrayList<>();
        if (food.isEmpty()) {
            return result;
        }
        Collections.sort(food, (f1, f2) -> (f1.price - f2.price));
        List<String> list = new ArrayList<>();
        int pos = 0, sum = 0;

        dfsFindAllMulti(coins, pos, sum, food, result, list);
        return result;
    }

    private static void dfsFindAllMulti(int coins, int pos, int sum,
                                        List<Food> food, List<List<String>> result, List<String> list) {
        result.add(new ArrayList<>(list));
        if (pos == food.size()) {
            return;
        }
        for (int i = pos; i < food.size(); i++) {
            if (sum + food.get(i).price > coins) {
                break;
            }
            list.add(food.get(i).name);
            dfsFindAllMulti(coins, i, sum + food.get(i).price, food, result, list);
            list.remove(list.size() - 1);
        }
    }

    public static List<List<String>> buyFav(int coins, List<Food> food, Food fav) {
        List<List<String>> result = new ArrayList<>();
        if (food.isEmpty() || fav.price > coins) {
            return result;
        }
        Collections.sort(food, (f1, f2) -> (f1.price - f2.price));
        List<String> list = new ArrayList<>();
        int pos = 0, sum = fav.price;
        list.add(fav.name);
        dfsFindAllFav(coins, pos, sum, food, result, list);
        return result;
    }

    private static void dfsFindAllFav(int coins, int pos, int sum, List<Food> food,
                               List<List<String>> result, List<String> list) {
        result.add(new ArrayList<>(list));
        for (int i = pos; i < food.size(); i++) {
            if (sum + food.get(i).price > coins) {
                break;
            }
            list.add(food.get(i).name);
            dfsFindAllFav(coins, i, sum + food.get(i).price, food, result, list);
            list.remove(list.size() - 1);
        }
    }

    static class Food {
        String name;
        int price;
        public Food(int price, String name) {
            this.name = name;
            this.price = price;
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
//        System.out.println(buyFood2(coins, foods));
//        System.out.println(buyFood(coins, foods));
        System.out.println(buyFav(coins, foods, new Food(300, "banana")));
//        System.out.println(buyFoodMulti(coins, foods));
    }
}
