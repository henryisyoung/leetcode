package airbnb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuCombinationSum {
    private static void search(List<List<Double>> res, int[] centsPrices, int start, int centsTarget,
                               List<Double> curCombo, double[] prices) {
        if (centsTarget == 0) {
            res.add(new ArrayList<>(curCombo));
            return;
        }
        for (int i = start; i < centsPrices.length; i++) {
            if (i > start && centsPrices[i] == centsPrices[i - 1]) {
                continue;
            }
            if (centsPrices[i] > centsTarget) {
                break;
            }
            curCombo.add(prices[i]);
            search(res, centsPrices, i + 1, centsTarget - centsPrices[i], curCombo, prices);
            curCombo.remove(curCombo.size() - 1);
        }
    }

    public static List<List<Double>> getCombos(double[] prices, double target) {
        List<List<Double>> res = new ArrayList<>();
        if (prices == null || prices.length == 0 || target <= 0) {
            return res;
        }
        int centsTarget = (int) Math.round(target * 100);
        Arrays.sort(prices);
        int[] centsPrices = new int[prices.length];
        for (int i = 0; i < prices.length; i++) {
            centsPrices[i] = (int) Math.round(prices[i] * 100);
        }
        search(res, centsPrices, 0, centsTarget, new ArrayList<Double>(), prices);
        return res;
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
        List<List<Double>> result = getCombos2(prices, 8.59);
        for (int i = 0; i < result.size(); i++) {
            System.out.println(i + "th result:");
            for (int j = 0; j < result.get(i).size(); j++) {
                System.out.println(result.get(i).get(j));
            }
        }
    }
}