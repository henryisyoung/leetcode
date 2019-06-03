package leetcode.graphAndSearch.backTracking;

import java.util.*;

public class AdditiveNumber {
    public boolean isAdditiveNumber(String num) {
        boolean result = false;
        List<Long> out = new ArrayList<>();
        helper(num, 0, out, result);
        return result;
    }

    void helper(String num, int start, List<Long> out, boolean res) {
        if (res){
            return;
        }
        if (start >= num.length()) {
            if (out.size() >= 3) {
                res = true;
            }
            return;
        }
        for (int i = start; i < num.length(); ++i) {
            String str = num.substring(start, i - start + 1);
            if ((str.length() > 1 && str.charAt(0) == '0') || str.length() > 19) {
                break;
            }
            long curNum = str.length() == 0 ? 0 : Long.parseLong(str);
            int n = out.size();
            if (out.size() >= 2 && curNum != out.get(n - 1) + out.get(n - 2)) {
                continue;
            }
            out.add(curNum);
            helper(num, i + 1, out, res);
            out.remove(out.size() - 1);
        }
    }
}
