package leetcode.graphAndSearch;

import java.util.*;

public class RemoveInvalidParentheses {
    public List<String> removeInvalidParentheses(String s) {
        List<String> res = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        visited.add(s);
        Queue<String> queue = new LinkedList<>();
        queue.add(s);
        boolean found = false;
        while (!queue.isEmpty()) {
            String t = queue.poll();
            if (isValid(t)) {
                res.add(t);
                found = true;
            }
            if (found) {
                continue;
            }
            for (int i = 0; i < t.length(); ++i) {
                if (t.charAt(i) != '(' && t.charAt(i) != ')') {
                    continue;
                }
                String str = t.substring(0, i) + t.substring(i + 1);
                if (!visited.contains(str)) {
                    queue.add(str);
                    visited.add(str);
                }
            }
        }
        return res;
    }

    private boolean isValid(String t) {
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

    List<String> removeInvalidParentheses2(String s) {
        List<String> res = new ArrayList<>();
        int cnt1 = 0, cnt2 = 0;
        for (char c : s.toCharArray()) {
            cnt1 += c == '(' ? 1 : 0;
            if (cnt1 == 0) cnt2 += c == ')' ? 1 : 0;
            else cnt1 -= c == ')' ? 1 : 0;
        }
        helper(s, 0, cnt1, cnt2, res);
        return res;
    }
    void helper(String s, int start, int cnt1, int cnt2, List<String> res) {
        if (cnt1 == 0 && cnt2 == 0) {
            if (isValid(s)) res.add(s);
            return;
        }
        for (int i = start; i < s.length(); ++i) {
            if (i != start && s.charAt(i) == s.charAt(i - 1)){
                continue;
            }
            if (cnt1 > 0 && s.charAt(i) == '(') {
                helper(s.substring(0, i) + s.substring(i + 1), i, cnt1 - 1, cnt2, res);
            }
            if (cnt2 > 0 && s.charAt(i) == ')') {
                helper(s.substring(0, i) + s.substring(i + 1), i, cnt1, cnt2 - 1, res);
            }
        }
    }

    List<String> removeInvalidParentheses3(String s) {
        List<String> res = new ArrayList<>();
        helper(s, 0, 0, Arrays.asList('(', ')'), res);
        return res;
    }

    private void helper(String s, int lastI, int lastJ, List<Character> p, List<String> res) {
        int cnt = 0;
        for (int i = lastI; i < s.length(); ++i) {
            if (s.charAt(i) == p.get(0)) ++cnt;
            else if (s.charAt(i) == p.get(1)) --cnt;
            if (cnt >= 0) continue;
            for (int j = lastJ; j <= i; ++j) {
                if (s.charAt(j) == p.get(1) && (j == lastJ || s.charAt(j) != s.charAt(j - 1))) {
                    helper(s.substring(0, j) + s.substring(j + 1), i, j, p, res);
                }
            }
            return;
        }
        String rev = new StringBuilder(s).reverse().toString();
        if (p.get(0) == '(') {
            helper(rev, 0, 0, Arrays.asList(')', '('), res);
        } else {
            res.add(rev);
        }
    }
}
