package apple;

import java.util.*;

public class LetterCombinationsOfPhoneNumber {
    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits == null || digits.length() == 0) return result;
        dfsSearchAll(digits, 0, result, "");
        return result;
    }

    private void dfsSearchAll(String digits, int pos, List<String> result, String s) {
        if (pos == digits.length()) {
            result.add(s);
            return;
        }
        int num = digits.charAt(pos) - '0';
        int size = num == 7 || num == 9 ? 4 : 3;

        int start = num <= 7 ? (num - 2) * 3 : (num - 2) * 3 + 1;

        for (int i = 0; i < size; i++) {
            char cur = (char) (start + i + 'a');
            dfsSearchAll(digits, pos + 1, result, s + cur);
        }

    }
}
