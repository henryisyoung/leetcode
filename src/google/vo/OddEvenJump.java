package google.vo;

import java.util.Map;
import java.util.TreeMap;

public class OddEvenJump {
    public static int oddEvenJumps(int[] arr) {
        int n = arr.length;
        int[][] dp = new int[n][2];
        dp[n - 1][0] = dp[n - 1][1] = 1;
        int result = 1;
        TreeMap<Integer, Integer> map = new TreeMap<>();
        map.put(arr[n - 1], n - 1);

        for (int i = n - 2; i >= 0; i--) {
            int curVal = arr[i];
            Map.Entry<Integer, Integer> upEntry = map.ceilingEntry(curVal);
            if (upEntry != null) {
                int index = upEntry.getValue();
                dp[i][0] = dp[index][1];
            }
            Map.Entry<Integer, Integer> downEntry = map.floorEntry(curVal);
            if (downEntry != null) {
                int index = downEntry.getValue();
                dp[i][1] = dp[index][0];
            }
            map.put(arr[i], i);
            result += dp[i][0];
        }
        return result;
    }

    public static void main(String[] args) {
        int[] arr = {5,1,3,4,2};
        System.out.println(oddEvenJumps(arr));
    }
}
