package apple;

import java.util.ArrayList;
import java.util.List;

public class Combinations {
    public static List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        if(n * k == 0) return result;
        findAll(n, k, 1, result, new ArrayList<>());
        return result;
    }

    private static void findAll(int n, int k, int pos, List<List<Integer>> result, ArrayList<Integer> list) {
        if (list.size() == k) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = pos; i <= n; i++) {
            list.add(i);
            findAll(n, k, i + 1, result, list);
            list.remove(list.size() - 1);
        }
    }

    public static void main(String[] args) {
        int n = 4, k = 2;
        List<List<Integer>> result = combine(n,k);
        System.out.println(result.toString());
    }
}
