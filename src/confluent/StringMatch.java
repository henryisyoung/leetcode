package confluent;

public class StringMatch {

    // https://leetcode.com/problems/wildcard-matching/submissions/
    public static boolean match(String s, String p) {
        int pos = p.indexOf("*");
        if (pos == -1) {
            return s.equals(p);
        }
        String a = p.substring(0, pos), b = p.substring(pos + 1);
        int i = 0;
        if (a.length() + b.length() > s.length()) {
            return false;
        }
        while (i < a.length()) {
            if (s.charAt(i) != a.charAt(i)) {
                return false;
            }
            i++;
        }
        int j = 0, n = s.length(), bLen = b.length();
        while (j < bLen) {
            if (s.charAt(n - 1 - j) != b.charAt(bLen - 1 - j)) {
                return false;
            }
            j++;
        }
        return true;
    }

    public static boolean matchWith(String s, String p) {
        int pos = p.indexOf("*");
        if (pos == -1) {
            System.out.println(pos + " pos ");
            return s.equals(p);
        }
        String first = p.substring(0, pos), second = p.substring(pos + 1);
        if (first.length() + second.length() > s.length()) {
            return false;
        }
        return s.startsWith(first) && s.endsWith(second);
    }

    public boolean matchTwo(String s, String p) {
        int i = 0, j = 0, m = s.length(), n = p.length();
        int star = -1, match = 0;
        while (i < m) {
            if (j < n && s.charAt(i) == p.charAt(j)) {
                i++;
                j++;
            } else if (j < n && p.charAt(j) == '*') {
                star = j;
                j++;
                match = i;
            } else if (star != -1) {
                match++;
                i = match;
                j = star + 1;
            } else {
                return false;
            }
        }
        while (j < n) {
            if (p.charAt(j++) != '*') {
                return false;
            }
        }
        return true;
    }
    public static boolean matchTwo2(String s, String p) {
        int i = 0, j = 0, star = -1, match = 0, m = s.length(), n = p.length();
        while (i < m) {
            if (j < n && s.charAt(i) == p.charAt(j)) {
                i++;
                j++;
            } else if (j < n && p.charAt(j) == '*') {
                star = j;
                match = i;
                j = star + 1;
            } else if (star != -1) {
                match++;
                i = match;
                j = star + 1;
            } else {
                return false;
            }
        }
        while (j < n) {
            if (p.charAt(j++) != '*') {
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args) {
        String s = "aasdsadfasdfsdf";
        String p = "aasdsadfasdfsdf*";
        System.out.println(matchWith(s, p));
    }
}
