package airbnb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuCombinationSum {
    public static List<List<Double>> getCombos(double[] prices, double target) {
        List<List<Double>> result = new ArrayList<>();
        if (prices == null || prices.length == 0) {
            return result;
        }
        int n = prices.length;
        Arrays.sort(prices);
        int roundtarget = (int) Math.round(target * 100);
        int[] roundPrices = new int[n];
        for (int i = 0; i < n; i++) {
            roundPrices[i] = (int) Math.round(100 * prices[i]);
        }
        search(result, roundPrices, roundtarget, prices, new ArrayList<Double>(), 0, 0);
        return result;
    }

    private static void search(List<List<Double>> result, int[] roundPrices, int roundtarget, double[] prices,
                               ArrayList<Double> list, int pos, int sum) {
        if (sum == roundtarget) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = pos; i < prices.length; i++) {
            if (i != pos && prices[i] == prices[i - 1]) {
                continue;
            }
            if (sum + roundPrices[i] > roundtarget) {
                break;
            }
            list.add(prices[i]);
            search(result, roundPrices, roundtarget, prices, list, i + 1, sum + roundPrices[i]);
            list.remove(list.size() - 1);
        }
    }

    public static List<List<Double>> getCombos2(double[] prices, double target) {
        List<List<Double>> result = new ArrayList<>();
        if (prices == null || prices.length == 0) {
            return result;
        }
        Arrays.sort(prices);
        int n = prices.length;
        int[] newPrices = new int[n];
        int newTarget = (int) Math.round(target * 100);
        for (int i = 0; i < n; i++) {
            newPrices[i] = (int) Math.round(prices[i] * 100);
        }
        dfsFindCombination(newPrices, newTarget, result, new ArrayList<Double>(), 0, 0, prices);
        return result;
    }

    private static void dfsFindCombination(int[] newPrices, int newTarget, List<List<Double>> result, ArrayList<Double> list,
                                           int sum, int pos, double[] prices) {
        if (sum == newTarget) {
            result.add(new ArrayList<Double>(list));
            return;
        }
        for (int i = pos; i < newPrices.length; i++) {
            if (i != pos && newPrices[i] == newPrices[i - 1]) {
                continue;
            }
            if (sum + newPrices[i] > newTarget) {
                break;
            }
            list.add(prices[i]);
            dfsFindCombination(newPrices, newTarget, result, list, sum + newPrices[i], i + 1, prices);
            list.remove(list.size() - 1);
        }
    }

    public static void main(String[] args) {
        double[] prices = {2.40, 0.01, 6.00, 2.58};
        List<List<Double>> result = getCombos(prices, 8.59);
        for (int i = 0; i < result.size(); i++) {
            System.out.println(i + "th result:");
            for (int j = 0; j < result.get(i).size(); j++) {
                System.out.println(result.get(i).get(j));
            }
        }
    }
}