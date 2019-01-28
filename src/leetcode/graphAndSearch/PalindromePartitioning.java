package leetcode.graphAndSearch;

import java.util.ArrayList;
import java.util.List;

public class PalindromePartitioning {
    public List<List<String>> partition(String s) {
        List<List<String>> result = new ArrayList<>();
        partitionHelper(0, result, new ArrayList<String>(), s);
        return result;
    }

    private void partitionHelper(int pos, List<List<String>> result, ArrayList<String> list, String s) {
        if (pos == s.length()) {
            result.add(new ArrayList<String>(list));
            return;
        }
        for (int i = pos + 1; i <= s.length(); i++) {
            String str = s.substring(pos, i);
            if (isPalindrome(str)) {
                list.add(str);
                partitionHelper(i, result, list, s);
                list.remove(list.size() - 1);
            }
        }
    }

    private boolean isPalindrome(String s) {
        int i = 0, j = s.length() - 1;
        while (i < j) {
            if (s.charAt(i) != s.charAt(j)) {
                return false;
            }
            i++;
            j--;
        }
        return true;
    }
}
