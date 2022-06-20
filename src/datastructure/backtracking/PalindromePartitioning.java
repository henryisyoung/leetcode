package datastructure.backtracking;

import java.util.ArrayList;
import java.util.List;

public class PalindromePartitioning {
    public List<List<String>> partition(String s) {
        List<List<String>> result = new ArrayList<>();
        if (s == null || s.length() == 0) return result;
        helper(result, new ArrayList<>(), 0, s);
        return result;
    }

    private void helper(List<List<String>> result, ArrayList<String> list, int pos, String s) {
        if (pos == s.length()) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = pos + 1; i <= s.length(); i++) {
            String str = s.substring(pos, i);
            if (isPalindrome(str)) {
                list.add(str);
                helper(result, list, i, s);
                list.remove(list.size() - 1);
            }
        }
    }

    private boolean isPalindrome(String str) {
        int l = 0, r = str.length() - 1;
        while (l < r) {
            if (str.charAt(l++) != str.charAt(r--)) return false;
        }
        return true;
    }
}
