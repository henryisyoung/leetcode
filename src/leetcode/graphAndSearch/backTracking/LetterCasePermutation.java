package leetcode.graphAndSearch.backTracking;

import java.util.ArrayList;
import java.util.List;

public class LetterCasePermutation {
    public List<String> letterCasePermutation(String S) {
        List<String> result = new ArrayList<>();
        dfsSearchAll(0, S, "", result);
        return result;
    }

    private void dfsSearchAll(int pos, String s, String cur, List<String> result) {
        if (s.length() == cur.length()) {
            result.add(cur);
            return;
        }
        for (int i = pos; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                dfsSearchAll(i + 1, s , cur + c, result);
            } else {
                dfsSearchAll(i + 1, s, cur + Character.toUpperCase(c), result);
                dfsSearchAll(i + 1, s, cur + Character.toLowerCase(c), result);
            }
        }
    }

    public static void main(String[] args) {
        String S = "a1b2";
        LetterCasePermutation solver = new LetterCasePermutation();
        List<String> result = solver.letterCasePermutation(S);
        System.out.println(result.toString());
    }
}
