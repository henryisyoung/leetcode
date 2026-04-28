package recovery;

import java.util.ArrayList;
import java.util.List;

public class GenerateParentheses {
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();

        createAllCases(0, 0, n, "", result);
        return  result;
    }

    private void createAllCases(int left, int right, int n, String curStr, List<String> result) {
        if (left == n && right == n) {
            result.add(curStr);
            return;
        }

        if (left > right) {
            createAllCases(left , right + 1, n, curStr + ")", result);
        }

        if (left < n) {
            createAllCases(left + 1, right , n, curStr + "(", result);
        }
    }
}
