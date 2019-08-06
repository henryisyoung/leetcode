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
        if (pos == num.length() ) {
            if (target == val){
                result.add(new String(s));
            }
            return;
        }
        for (int i = pos + 1; i <= num.length(); i++) {
            String str = num.substring(pos, i);
            if(str.length() > 1 && str.charAt(0) == '0'){
                return;
            }
            Long cur = Long.parseLong(str);
            if (s.length() == 0) {
                dfsSearchAll(num, target, i, cur, val + cur, result, str); // +
            } else {
                dfsSearchAll(num, target, i, -cur, val - cur, result, s + "-" + cur); // -
                dfsSearchAll(num, target, i, cur, val + cur, result, s + "+" + cur); // +
                dfsSearchAll(num, target, i, prev * cur, val - prev + prev * cur, result, s + "*" + cur);
            }
        }
    }

    public static void main(String[] args) {
        ExpressionAddOperators solver = new ExpressionAddOperators();
        List<String> result = solver.addOperators("123", 6);
        System.out.println(result.toString());
    }
}
