package apple;

import java.util.ArrayList;
import java.util.List;

public class FactorCombinations {
    public static List<List<Integer>> getFactors(int n) {
        List<List<Integer>> result = new ArrayList<>();
        if (n <= 1) return result;
        dfsFind(n, result, new ArrayList<>(), 2);
        return result;
    }

    private static void dfsFind(int n, List<List<Integer>> result, ArrayList<Integer> list, int pos) {
        if (n == 1) {
            return;
        }
        int start = list.size() == 0 ? 2 : list.get(list.size() - 1);
        for(int i = start; i * i <= n; i++) {
            if(n % i ==0) {
                list.add(i);
                dfsFind(n / i, result, list, i);
                list.add(n / i);
                result.add(new ArrayList<>(list));
                list.remove(list.size() - 1);
                list.remove(list.size() - 1);
            }
        }
    }

    public static void main(String[] args) {
        int n = 8;
        System.out.println(getFactors(12));
    }
}
