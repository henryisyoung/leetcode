package datastructure.dp;

import java.util.HashMap;
import java.util.Map;

public class BurstBalloons {
    public int maxCoins(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int n = nums.length;
        int[] array = new int[n + 2];
        array[0] = array[n + 1] = 1;
        for (int i = 0; i < n; i++) {
            array[i + 1] = nums[i];
        }

        Map<String, Integer> map = new HashMap<>();
        return memoSearch(map, array, 0, n + 1);
    }

    private int memoSearch(Map<String, Integer> map, int[] array, int l, int r) {
        if (l == r) {
            return 0;
        }
        String str = l + "-" + r;
        if (map.containsKey(str)) {
            return map.get(str);
        }
        int val = 0;
        for (int k = l + 1; k < r; k++) {
            int left = memoSearch(map, array, l, k);
            int right = memoSearch(map, array, k, r);
            val = Math.max(val, left + right + array[l] * array[k] * array[r]);
        }
        map.put(str, val);
        return val;
    }
}
