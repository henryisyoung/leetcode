package datastructure.backtracking;

import java.util.ArrayList;
import java.util.List;

public class GenerateParentheses {

    public static void main(String[] args) {
        generateParenthesis(3);
    }

    public static List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        if(n == 0) return result;
        helper(result, n, n, "");
        return result;
    }

    private static void helper(List<String> result, int left, int right, String str) {
        if (left == 0 && right == 0) {
            System.out.println("str: " + str);
            result.add(str);
            return;
        }
        if(left > 0) {
            helper(result, left - 1, right, str + "(");
        }

        if(right > 0 && right > left) {
            helper(result, left, right - 1, str + ")");
        }

    }
}
