package airbnb.Phone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CombinationSum {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        List<Integer> list = new ArrayList<>();
        dfsFindAll(candidates, target, list, result, 0, 0);
        return result;
    }

    private void dfsFindAll(int[] candidates, int target, List<Integer> list,
                            List<List<Integer>> result, int sum, int pos) {
        if (sum == target) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = pos; i < candidates.length; i++) {
            if (sum + candidates[i] > target) {
                break;
            }
            list.add(candidates[i]);
            dfsFindAll(candidates, target, list, result, sum + candidates[i], i);
            list.remove(list.size() - 1);
        }
    }

    static class Food {
        String name;
        double price;
        int intPrice;
        public Food(double price, String name) {
            this.name = name;
            this.price = price;
            this.intPrice = (int) (price * 100);
        }
    }

    public static List<List<String>> menuCombinations(double[] prices, String[] names, double target) {
        List<Food> foods = new ArrayList<>();
        for (int i = 0; i < prices.length; i++) {
            foods.add(new Food(prices[i], names[i]));
        }
        Collections.sort(foods, (a, b) -> (a.intPrice - b.intPrice));
        List<List<String>> result = new ArrayList<>();
        List<String> list = new ArrayList<>();
        dfsFindFood(result, list, 0, 0, (int) (100 * target), foods);
        return result;
    }

    private static void dfsFindFood(List<List<String>> result, List<String> list, int pos, int sum, int target, List<Food> foods) {
        if (sum == target) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = pos ; i < foods.size(); i++) {
            Food food = foods.get(i);
            int curPrice = food.intPrice;
            if (curPrice + sum > target) {
                break;
            }
            list.add(food.name);
            dfsFindFood(result, list, i, sum + curPrice, target, foods);
            list.remove(list.size() - 1);
        }
    }

    public static void main(String[] args) {
        double[] prices = {77.22, 33.11, 11.33, 77.22};
        String[] names = {"apple", "coca", "pizza", "beef"};
        double target = 77.22 ;
        System.out.println(menuCombinations(prices, names, target));
    }
}
