package facebook;

import java.util.ArrayList;
import java.util.List;

public class _RemoveInvalidParentheses {
    public static List<String> removeInvalidParentheses(String s) {
        List<String> result = new ArrayList<>();
        int extraLeft = 0, extraRight = 0;
        for (char c : s.toCharArray()) {
            extraLeft += c == '(' ? 1 : 0;
            if (c == ')') {
                if (extraLeft == 0) extraRight++;
                else extraLeft--;
            }
        }
        findAll(extraLeft, extraRight, 0, result, s);
        return result;
    }

    private static void findAll(int extraLeft, int extraRight, int pos, List<String> result, String s) {
        if (extraLeft == extraRight && extraLeft == 0) {
            if (isValid(s)) result.add(s);
            return;
        }
        for (int i = pos; i < s.length(); i++) {
            if (i != pos && s.charAt(i) == s.charAt(i - 1)) continue;
            if (s.charAt(i) == '(' && extraLeft > 0) {
                findAll(extraLeft - 1, extraRight, i, result,s.substring(0, i) + s.substring(i + 1));
            }
            if (s.charAt(i) == ')' && extraRight > 0) {
                findAll(extraLeft, extraRight - 1, i, result,s.substring(0, i) + s.substring(i + 1));
            }
        }
    }

    private static boolean isValid(String t) {
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
