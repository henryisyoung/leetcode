package airbnb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuCombinationSum {
    private void search(List<List<Double>> res, int[] centsPrices, int start, int centsTarget,
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

    public List<List<Double>> getCombos(double[] prices, double target) {
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

    public List<List<Double>> getCombos2(double[] prices, double target) {
        List<List<Double>> result = new ArrayList<>();
        int newTarget = (int) Math.round(target * 1000);
        int[] newPrices = new int[prices.length];
        for (int i = 0; i < prices.length; i++) {
            double price = prices[i];
            newPrices[i] = (int) Math.round(1000 * price);
        }
        Arrays.sort(newPrices);
        List<Double> list = new ArrayList<>();
        dfsFindCombination(result, prices, 0, newTarget, list, 0, newPrices);
        return result;
    }

    private void dfsFindCombination(List<List<Double>> result, double[] prices, int sum, int target,
                                    List<Double> list, int pos, int[] newPrices) {
        if (sum == target) {
            result.add(new ArrayList<Double>(list));
            return;
        }
        for (int i = pos; i < newPrices.length; i++) {
            if (i > 0 && newPrices[i] == newPrices[i - 1]) {
                continue;
            }
            if (sum + newPrices[i] > target) {
                break;
            }
            list.add(prices[i]);
            dfsFindCombination(result, prices, sum + newPrices[i], target, list, i + 1, newPrices);
            list.remove(list.size() - 1);
        }
    }
}