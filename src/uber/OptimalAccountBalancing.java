package uber;

import java.util.*;

public class OptimalAccountBalancing {
    public int minTransfers(int[][] transactions) {
        if (transactions == null || transactions.length == 0 || transactions[0] == null || transactions[0].length == 0) {
            return 0;
        }
        Map<Integer, Integer> map = new HashMap<>();
        for (int[] t : transactions) {
            int val = t[2];
            int m = t[1], n = t[0];
            map.put(n, map.getOrDefault(n, 0) - val);
            map.put(m, map.getOrDefault(m, 0) + val);
        }
        List<Integer> list = new ArrayList<>();
        for (int key : map.keySet()) {
            if (map.get(key) != 0) list.add(map.get(key));
        }
        Integer[] account = new Integer[list.size()];
        account = list.toArray(account);
        return dfsSearchAll(account, 0, 0);
    }

    private int dfsSearchAll(Integer[] account, int count, int pos) {
        while (pos < account.length && account[pos] == 0) pos++;
        if (pos == account.length) return count;
        int result = Integer.MAX_VALUE;
        for (int i = pos + 1; i < account.length; i++) {
            if (account[pos] * account[i] < 0) {
                account[i] += account[pos];
                result = Math.min(result, dfsSearchAll(account, count + 1, pos + 1));
                account[i] -= account[pos];
            }
        }
        return result;
    }
}
