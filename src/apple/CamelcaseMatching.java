package apple;

import java.util.ArrayList;
import java.util.List;

public class CamelcaseMatching {
    public static List<Boolean> camelMatch(String[] queries, String pattern) {
        List<Boolean> result = new ArrayList<>();
        if (queries == null || queries.length == 0) return result;
        for (String str : queries) {
            if (validPattern(str, pattern)) {
                result.add(true);
            } else {
                result.add(false);
            }
        }
        return result;
    }

    private static boolean validPattern(String query, String pattern) {
        if (pattern.length() > query.length()) return false;
        int i = 0, j = 0;
        while (i < query.length() && j < pattern.length()) {
            char c1 = query.charAt(i), c2 = pattern.charAt(j);
            if (c1 == c2) {
                i++;
                j++;
            } else {
                if (Character.isUpperCase(c1)) {
                    return false;
                }
                i++;
            }
        }
        while (i < query.length()) {
            char c1 = query.charAt(i++);
            if (Character.isUpperCase(c1)) {
                return false;
            }
        }
        return j == pattern.length();
    }

    public static void main(String[] args) {
        System.out.println(validPattern("FooBarTest", "Foo"));
    }
}
