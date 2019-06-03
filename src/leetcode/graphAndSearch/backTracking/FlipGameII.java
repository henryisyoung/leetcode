package leetcode.graphAndSearch.backTracking;

import java.util.*;

public class FlipGameII {
    Map<String, Boolean> map = new HashMap<>();
    public boolean canWin(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        if (map.containsKey(s)) {
            return map.get(s);
        }
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == '+' && s.charAt(i + 1) == '+') {
                String str = s.substring(0, i) + "--" + s.substring(i + 2);
                if(!canWin(str)){
                    return true;
                }
                map.put(str, true);
            }

        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println("123".substring(3).equals(""));
    }
}
