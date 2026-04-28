package recovery;

import java.util.ArrayList;
import java.util.List;

public class PalindromePartitioning {
    public List<List<String>> partition(String s) {
        List<List<String>> result = new ArrayList<>();
        if (s == null || s.equals("")) return result;

        dfsFindAllPartition(s, result, new ArrayList<>(), 0);
        return result;
    }

    private void dfsFindAllPartition(String s, List<List<String>> result, ArrayList<String> list, int pos) {
        if (pos == s.length()) {
            result.add(new ArrayList<>(list));
            return;
        }

        for (int i = pos + 1; i <= s.length(); i++) {
            String subStr = s.substring(pos, i);
            if (isPalindrome(subStr)) {
                list.add(subStr);
                dfsFindAllPartition(s, result, list, i);
                list.removeLast();
            }
        }
    }

    private boolean isPalindrome(String subStr) {
        int i = 0, j = subStr.length() - 1;
        while (i < j) {
            if (subStr.charAt(i) != subStr.charAt(j)) return false;
            i++;
            j--;
        }
        return true;
    }
}
