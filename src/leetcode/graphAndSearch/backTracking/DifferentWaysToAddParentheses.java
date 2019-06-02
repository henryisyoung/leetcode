package leetcode.graphAndSearch.backTracking;

import java.util.*;

public class DifferentWaysToAddParentheses {
    Map<String, List<Integer>> map = new HashMap<>();
    public List<Integer> diffWaysToCompute(String input) {
        if (map.containsKey(input)) {
            return map.get(input);
        }
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '+' || c == '-' || c == '*') {
                List<Integer> left = diffWaysToCompute(input.substring(0, i));
                List<Integer> right = diffWaysToCompute(input.substring(i + 1));
                for (int l : left) {
                    for (int r : right) {
                        if (c == '+') result.add(l + r);
                        else if (c == '-') result.add(l - r);
                        else result.add(l * r);
                    }
                }
            }
        }

        if (result.size() == 0) {
            result.add(Integer.parseInt(input));
        }
        map.put(input, result);
        return result;
    }

    public static void main(String[] args) {
        DifferentWaysToAddParentheses solver = new DifferentWaysToAddParentheses();
        List<Integer> result = solver.diffWaysToCompute("2*3-4*5");
        System.out.println(result.toString());
    }
}
