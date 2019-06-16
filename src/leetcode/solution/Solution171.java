package leetcode.solution;

public class Solution171 {
    public int titleToNumber(String s) {
        int rst = 0;
        if (s==null) return rst;
        int n = s.length();
        for (int i=0; i<n; ++i) {
            char ch = s.charAt(i);
            rst = rst * 26 + ch - 'A' + 1;
        }
        return rst;
    }
}
