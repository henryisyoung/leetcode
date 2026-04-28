package recovery;

import java.util.ArrayList;
import java.util.List;

public class LetterCombinationsPhoneNumber {
    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        int len = digits.length();
        findAllStr(0, len, digits, "", result);
        return result;
    }

    private void findAllStr(int index, int len, String digits, String curStr, List<String> result) {
        if (index == len) {
            result.add(curStr);
            return;
        }

        int number = digits.charAt(index) - '0';
        int size = number == 7 || number == 9 ? 4 : 3;
        int start = number > 7 ? (number - 2) * 3 + 1 : (number - 2) * 3;
        for (int i = 0; i < size; i++) {
            char curChar = (char) ('a' + start + i);
            findAllStr(index + 1, len, digits, curStr + curChar, result);
        }
    }
}
