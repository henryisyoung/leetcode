package airbnb;

import java.util.*;

public class MenuCombinationSumItem {

    public List<List<String>> findCombinations(double[] prices, String[] items, double budget) {
        List<List<String>> result = new ArrayList<>();
        if (prices == null || items == null || prices.length != items.length) {
            return result;
        }

        int n = prices.length;
        Item[] arr = new Item[n];

        for (int i = 0; i < n; i++) {
            double price = prices[i];
            int iPrice = (int) (price * 100);
            String name = items[i];
            arr[i] = new Item(price, iPrice, name);
        }

        Arrays.sort(arr, new Comparator<Item>(){
            public int compare(Item i1, Item i2) {
                return i1.iPrice - i2.iPrice;
            }
        });
        int target = (int) (100 * budget);
        List<String> list = new ArrayList<>();

        dfsSearch(result, list, 0, target, 0, arr);
        return result;
    }


    private void dfsSearch(List<List<String>> result, List<String> list, int sum, int target, int pos,
                           Item[] items) {
        if (sum == target) {
            result.add(new ArrayList<String>(list));
            return;
        }
        for(int i = pos; i < items.length; i++) {
            if(sum + items[i].iPrice > target) {
                break;
            }
            list.add(items[i].name);
            dfsSearch(result, list, sum + items[i].iPrice, target, i + 1, items);
            list.remove(list.size() - 1);
        }
    }

    private class Item {
        double price;
        int iPrice;
        String name;
        public Item (double price, int iPrice, String name) {
            this.iPrice = iPrice;
            this.price = price;
            this.name = name;
        }

        @Override
        public String toString() {
            return "name " + name + " price " + price + " iPrice " + iPrice;
        }
    }

    public static void main(String[] args) {
        double[] prices = {1.22, 2.11, 3.01, 2.44, 2.11};
        String[] items = {"wings", "soda", "burger", "pizza", "salad"};
        double budget = 6.34;

        MenuCombinationSumItem solver = new MenuCombinationSumItem();
        List<List<String>> result = solver.findCombinations(prices, items, budget);
        for(List<String> list : result) {
            System.out.print(list.toString());
        }
    }
}