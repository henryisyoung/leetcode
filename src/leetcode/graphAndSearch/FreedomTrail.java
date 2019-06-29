package leetcode.graphAndSearch;

import java.util.*;

public class FreedomTrail {
    public int findRotateSteps(String ring, String key) {
        Map<String, Integer> map = new HashMap<>();
        return findRotateStepsHelper(ring, key, map);
    }

    private int findRotateStepsHelper(String ring, String key, Map<String, Integer> map) {
        if (key.length() == 0) {
            return 0;
        }
        String k = ring + "##" + key;
        if (map.containsKey(k)) {
            return map.get(k);
        }
        int count = 1;
        char c = key.charAt(0);
        int i = 0;
        while (i < ring.length() && ring.charAt(i) != c) {
            i++;
        }
        int j = ring.length() - 1;
        while (j > 0 && ring.charAt(j) != c) {
            j--;
        }

        int step = Math.min(ring.length() - j, i);
        key = key.substring(1);
        if (step == i) {
            ring = ring.substring(i) + ring.substring(0, i);
        } else {
            ring = ring.substring(j) + ring.substring(0, j);
        }
        map.put(k, count + step + findRotateSteps(ring, key));
        return map.get(k);
    }


//    Basic idea is we pick a char from key and for each occurance of it in ring, we align that char at 12:00 and check
//    the cost for the next char from key. Along the way, we add up all the costs for all chars in key and pick the min.
//    dp[i][j] indicates minimum cost for substring key[0..j] in the ring state i (that is ith char is aligned at 12:00,
//    there are only ring.length states possible.).
    Integer[][] dp = null;
    public int findRotateStepsDP(String ring, String key) {
        dp = new Integer[ring.length()][key.length()];
        return steps(0, ring, 0, key);
    }
    private int steps(int i, String ring, int j, String key){
        if(j == key.length()) {
            return 0;
        }
        if(dp[i][j] != null){
            return dp[i][j];
        }
        dp[i][j] = Integer.MAX_VALUE;
        char ch = key.charAt(j);
        for(int k=0; k < ring.length(); k++){
            int tmp = (k+i) % ring.length();
            if(ring.charAt(tmp) == ch)
                dp[i][j] = Math.min(dp[i][j], 1 + Math.min(k, ring.length()-k) + steps(tmp, ring, j+1, key));
        }
        return dp[i][j];
    }

    public static void main(String[] args) {
        String ring = "ababcab" , key = "acbaacba";
        FreedomTrail sovler = new FreedomTrail();
        System.out.println(sovler.findRotateSteps(ring, key));
    }
}
