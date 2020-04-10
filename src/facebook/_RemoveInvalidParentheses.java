package facebook;

import java.util.ArrayList;
import java.util.List;

public class _RemoveInvalidParentheses {
    public static List<String> removeInvalidParentheses(String s) {
        List<String> result = new ArrayList<>();
        int extraLeft = 0, extraRight = 0;
        for (char c : s.toCharArray()) {
            extraLeft += c == '(' ? 1 : 0;
            if (extraLeft == 0) extraRight += c == ')' ? 1 : 0;
            else extraLeft -= c == ')' ? 1 : 0;
        }
        findAll(s, extraLeft, extraRight, 0, result);
        return result;
    }

    private static void findAll(String s, int extraLeft, int extraRight, int pos, List<String> result) {
        if (extraLeft == extraRight && extraLeft == 0) {
            if (valid(s)) {
                result.add(s);
                return;
            }
        }
        for (int i = pos; i < s.length(); i++) {
            if (i != pos && s.charAt(i) == s.charAt(i - 1)){
                continue;
            }
            if (extraLeft > 0 && s.charAt(i) == '(') {
                findAll(s.substring(0, i) + s.substring(i + 1), extraLeft - 1, extraRight, i, result);
            }
            if (extraRight > 0 && s.charAt(i) == ')') {
                findAll(s.substring(0, i) + s.substring(i + 1), extraLeft, extraRight - 1, i, result);
            }
        }
    }

    private static boolean valid(String t) {
        int cnt = 0;
        for (int i = 0; i < t.length(); ++i) {
            if (t.charAt(i) == '(') {
                ++cnt;
            }
            else if (t.charAt(i) == ')' && --cnt < 0) {
                return false;
            }
        }
        return cnt == 0;
    }

    public static void main(String[] args) {
        System.out.println(removeInvalidParentheses("(a)())()"));
    }
}
