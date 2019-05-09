package pinterest;

import java.util.*;
//https://blog.csdn.net/liuchonge/article/details/76210163
public class IsSubsequence {
    public boolean isSubsequenceDP(String t, String s) {
        if (s == null || t == null || s.length() < t.length()) {
            return false;
        }
        int m = s.length(), n = t.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = 1;
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + dp[i - 1][j];
                } else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }
        return dp[m][n] > 0;
    }

    public boolean isSubsequenceTwoPointer(String s, String t) {
        if (s == null || t == null || s.length() > t.length()) {
            return false;
        }
        if (s.length() == 0) {
            return true;
        }
        int m = s.length(), n = t.length();
        int i = 0, j = 0;
        while (i < n) {
            if (s.charAt(j) == t.charAt(i)) {
                j++;
            }
            if (j == m) {
                return true;
            }
            i++;
        }
        return false;
    }
    public boolean isSubsequenceTwoPointerFaster(String s, String t) {
        if (s == null || t == null || s.length() > t.length()) {
            return false;
        }
        if (s.length() == 0) {
            return true;
        }
        int pre = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int pos = t.indexOf(c, pre);
            if (pos == -1) {
                return false;
            }
            pre = pos + 1;
        }
        return true;
    }

    public boolean isSubsequenceFollowUp(String s, String t) {
        if (s == null || t == null || s.length() > t.length()) {
            return false;
        }
        if (s.length() == 0) {
            return true;
        }
        Map<Character, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < t.length(); i++){
            char c = t.charAt(i);
            if (!map.containsKey(c)) {
                map.put(c, new ArrayList<>());
            }
            map.get(c).add(i);
        }
        int prev = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (map.get(c) == null) {
                return false;
            } else {
                List<Integer> indexs = map.get(c);
                int pos = findFirstLargerOrEqual(prev, indexs);
                if (pos == -1) {
                    return false;
                }
                prev = pos + 1;
            }
        }
        return true;
    }

    private int findFirstLargerOrEqual(int index, List<Integer> list) {
        int start = 0, end = list.size() - 1;
        while (start <= end) {
            int mid = start + (end - start) / 2;
            if (list.get(mid) < index) {
                start = mid + 1;
            } else {
                end = mid - 1;
            }
        }

        return start == list.size() ? -1 : list.get(start);
    }


    public static void main(String[] args) {
        IsSubsequence solver = new IsSubsequence();
        String s = "acb", t = "ahbgdc";
        System.out.println(solver.isSubsequenceFollowUp(s, t));

//        List<Integer> indexs = Arrays.asList(1,2,4,5);
//        int val = 2;
//        System.out.println(solver.findFirstLargerOrEqual(val, indexs));
    }
}
