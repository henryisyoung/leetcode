package pinterest;

import java.util.*;

public class LetterCombinationsPhoneNumber {
    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits == null || digits.length() == 0) {
            return result;
        }
        dfsSearchAll(digits, result, "", 0);
        return result;
    }

    private void dfsSearchAll(String digits, List<String> result, String s, int pos) {
        if (pos == digits.length()) {
            result.add(s);
            return;
        }
        int num = digits.charAt(pos) - '0';
        int count = num == 7 || num == 9 ? 4 : 3;
        char start = (char) ((num - 2) * 3 + 'a');
        if (num > 7) {
            start = (char) (start + 1);
        }
//        System.out.println(start);
        for (int i = 0; i < count; i++) {
            dfsSearchAll(digits, result, s + (char) (start + i), pos + 1);
        }
    }

    public static void main(String[] args) {
        String digits = "23";
        LetterCombinationsPhoneNumber sovler = new LetterCombinationsPhoneNumber();
        System.out.println( sovler.letterCombinations(digits));
    }
}
