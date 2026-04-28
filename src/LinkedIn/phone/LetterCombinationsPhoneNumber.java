package LinkedIn.phone;

import java.util.ArrayList;
import java.util.List;

public class LetterCombinationsPhoneNumber {
    public List<String> letterCombinations(String digits) {
        if (digits == null || digits.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> results = new ArrayList<>();
        findAllWords(digits, "", results, 0);
        return results;
    }

    private void findAllWords(String digits, String str, List<String> results, int index) {
        if (index == digits.length()) {
            results.add(str);
            return;
        }
        int curNum = digits.charAt(index) - '0';
        int size = curNum == 7 || curNum == 9 ? 4 : 3;
        int start = curNum > 7 ? (curNum - 2) * 3 + 1 : (curNum - 2) * 3;

        for (int i = 0; i < size; i++) {
            char newChar = (char) ('a' + start + i);
            findAllWords(digits, str + newChar, results, index + 1);
        }
    }
}