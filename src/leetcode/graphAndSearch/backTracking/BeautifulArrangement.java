package leetcode.graphAndSearch.backTracking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BeautifulArrangement {
    int count = 0;
    public int countArrangement(int N) {
        if (N == 0) return 0;
        Set<Integer> set = new HashSet<>();
        dfsSearchAll(N, new ArrayList<>(), set);
        return count;
    }

    private void dfsSearchAll(int n, ArrayList<Integer> list, Set<Integer> set) {
        if (list.size() == n) {
            count++;
            return;
        }
        for (int i = 1; i <= n; i++) {
            if (!set.contains(i)) {
                int index = list.size() + 1;
                if (i % index == 0 || index % i == 0) {
                    list.add(i);
                    set.add(i);
                    dfsSearchAll(n, list, set);
                    list.remove(list.size() - 1);
                    set.remove(i);
                }
            }
        }
    }

    public static void main(String[] args) {
        int n = 2;
        BeautifulArrangement solver = new BeautifulArrangement();
        System.out.println(solver.countArrangement(n));
    }
}
