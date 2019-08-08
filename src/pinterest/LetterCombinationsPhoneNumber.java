package pinterest;

import java.util.*;

public class LetterCombinationsPhoneNumber {
    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits == null || digits.length() == 0) return result;
        dfsSearchAll(digits, 0, result, "");
        return result;
    }

    private void dfsSearchAll(String digits, int pos, List<String> result, String cur) {
        if (pos == digits.length()) {
            result.add(cur);
            return;
        }
        int num = digits.charAt(pos) - '0';
        int count = num == 7 || num == 9 ? 4 : 3;
        char start = (char) ((num - 2) * 3 + 'a');
        if (num > 7) {
            start = (char) (start + 1);
        }
        for (int i = 0; i < count; i++){
            dfsSearchAll(digits, pos + 1, result, cur + (char) (start + i));
        }
    }

    public static void main(String[] args) {
        String digits = "23";
        LetterCombinationsPhoneNumber sovler = new LetterCombinationsPhoneNumber();
        System.out.println( sovler.letterCombinations(digits));
    }
}
