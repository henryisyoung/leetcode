package uber;

import java.util.*;

public class OptimalAccountBalancing {
    public int minTransfers(int[][] transactions) {
        if (transactions == null || transactions.length == 0 || transactions[0] == null || transactions[0].length == 0) {
            return 0;
        }
        Map<Integer, Integer> map = new HashMap<>();
        for (int[] t : transactions) {
            map.put(t[0], map.getOrDefault(t[0], 0) - t[2]);
            map.put(t[1], map.getOrDefault(t[1], 0) + t[2]);
        }
        List<Integer> list = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() != 0) list.add(entry.getValue());
        }
        Integer[] account = new Integer[list.size()];
        account = list.toArray(account);
        return dfsSearchAll(account, 0, 0);
    }

    private int dfsSearchAll(Integer[] account, int pos, int count) {
        while (pos < account.length && account[pos] == 0) pos++;
        if (pos == account.length) return count;
        int result = Integer.MAX_VALUE;
        for (int i = pos + 1; i < account.length; i++) {
            if (account[pos] * account[i] < 0) {
                account[i] += account[pos];
                result = Math.min(result, dfsSearchAll(account, pos + 1, count + 1));
                account[i] -= account[pos];
            }
        }
        return result;
    }
}
