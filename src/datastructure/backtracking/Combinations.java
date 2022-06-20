package datastructure.backtracking;

import java.util.ArrayList;
import java.util.List;

public class Combinations {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result= new ArrayList<>();
        dfsFind(result, 1, n ,k, new ArrayList<>());
        return result;
    }

    private void dfsFind(List<List<Integer>> result, int pos, int n, int k, ArrayList<Integer> list) {
        if (list.size() == k) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = pos; i <= n; i++) {
            list.add(i);
            dfsFind(result, i + 1, n, k, list);
            list.remove(list.size() - 1);
        }
    }
}
