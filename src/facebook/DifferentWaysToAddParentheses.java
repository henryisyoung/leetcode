package facebook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DifferentWaysToAddParentheses {
    public static List<Integer> diffWaysToCompute(String input) {
        if (input == null || input.length() == 0) return new ArrayList<>();
        Map<String, List<Integer>> map = new HashMap<>();
        return findAll(input, map);
    }

    private static List<Integer> findAll(String input, Map<String, List<Integer>> map) {
        if (map.containsKey(input)) return map.get(input);
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '+' || c == '-' || c == '*') {
                List<Integer> left = findAll(input.substring(0, i), map);
                List<Integer> right = findAll(input.substring(i + 1), map);
                for (int l : left) {
                    for (int r : right) {
                        if (c == '*') {
                            result.add(l * r);
                        } else if (c == '-') {
                            result.add(l - r);
                        } else {
                            result.add(l + r);
                        }
                    }
                }
            }
        }
        if (result.size() == 0) result.add(Integer.parseInt(input));
        map.put(input, result);
        return result;
    }

    public static void main(String[] args) {
        String input = "2*3-4*5";
        System.out.println(diffWaysToCompute(input).toString());
    }
}
