package snap;

import java.util.ArrayList;
import java.util.List;

public class ExpressionAddOperators {
    public List<String> addOperators(String num, int target) {
        List<String> result = new ArrayList<>();
        dfsSearchAll(num, target, 0, 0, 0, result, "");
        return result;
    }

    private void dfsSearchAll(String num, int target, int pos, long prev, long val, List<String> result, String s) {
        if (pos == num.length()) {
            if (val == target) result.add(s);
            return;
        }
        for (int i = pos + 1; i <= num.length(); i++) {
            String cur = num.substring(pos, i);
            if (cur.length() > 1 && cur.charAt(0) == '0') continue;
            Long curval = Long.parseLong(cur);
            if (pos == 0) {
                dfsSearchAll(num, target, i, curval, val + curval, result, s + cur);
            } else {
                //+
                dfsSearchAll(num, target, i, curval, val + curval, result, s + "+" + cur);
                //-
                dfsSearchAll(num, target, i, -curval, val - curval, result, s + "-" + cur);
                //*
                dfsSearchAll(num, target, i, prev * curval, val - prev + prev * curval, result, s + "*" + cur);
            }
        }
    }

    public static void main(String[] args) {
        ExpressionAddOperators solver = new ExpressionAddOperators();
        List<String> result = solver.addOperators("123", 6);
        System.out.println(result.toString());
    }
}
