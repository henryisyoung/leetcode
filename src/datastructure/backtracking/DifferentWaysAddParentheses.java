package datastructure.backtracking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DifferentWaysAddParentheses {
    public List<Integer> diffWaysToCompute(String expression) {
        List<Integer> result = new ArrayList<>();
        if (expression == null || expression.length() == 0) return result;
        Map<String, List> map = new HashMap<>();

        return helper(map, expression);
    }

    private List<Integer> helper(Map<String, List> map, String expression) {
        List<Integer> result = new ArrayList<>();

        if (map.containsKey(expression)) {
            return map.get(expression);
        }
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '+' || c == '-' || c == '*') {
                List<Integer> left = helper(map, expression.substring(0, i));
                List<Integer> right = helper(map, expression.substring(i + 1));
                for (int l : left) {
                    for (int r : right) {
                        if (c == '+') {
                            result.add(l + r);
                        } else if (c == '-') {
                            result.add(l - r);
                        } else {
                            result.add(l * r);
                        }
                    }
                }
            }
        }
        if (result.size() == 0) result.add(Integer.parseInt(expression));
        map.put(expression, result);
        return result;
    }
}
