package uber.phone.leetcode;

import java.util.ArrayList;
import java.util.List;

public class RemoveInvalidParentheses {
    public static List<String> removeInvalidParentheses(String s) {
        List<String> result = new ArrayList<>();
        int extraLeft = 0, extraRight = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                extraLeft++;
            } else if (c == ')') {
                if (extraLeft == 0) {
                    extraRight++;
                } else {
                    extraLeft--;
                }
            }
        }
        dfsFindAll(extraLeft, extraRight, s, 0, result);
        return result;
    }

    private static void dfsFindAll(int extraLeft, int extraRight, String s, int pos, List<String> result) {
        if (extraLeft == 0 && extraRight == 0) {
            if (isValid(s) && !result.contains(s)) {
                result.add(s);
            }
            return;
        }
        for (int i = pos; i < s.length(); i++) {
            char c = s.charAt(i);
            if (i != pos && c == s.charAt(i - 1)) {
                continue;
            }
            if (c == '(') {
                dfsFindAll(extraLeft - 1, extraRight, s.substring(0, i) + s.substring(i + 1), i, result);
            } else if (c == ')') {
                dfsFindAll(extraLeft , extraRight - 1, s.substring(0, i) + s.substring(i + 1), i, result);
            }
        }
    }

    private static boolean isValid(String s) {
        int l = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                l++;
            } else if (c == ')') {
                l--;
                if (l < 0) {
                    return false;
                }
            }
        }
        return l == 0;
    }

    public static void main(String[] args) {
        String s = "()())()";
        System.out.println(removeInvalidParentheses(s));
    }
}
