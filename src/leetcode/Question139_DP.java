package leetcode;
import java.util.*;

public class Question139_DP {
    public boolean wordBreak(String s, List<String> wordDict) {
        if(s == null || s.length() == 0) return false;
        if(wordDict == null || wordDict.size() == 0) return false;
        int n = s.length();
        boolean[] f = new boolean[n+1];
        f[0] = true;
        int max = max(wordDict);
        for(int i = 1; i<=n; i++){
            for(int j = 1; j <= max && j <= i;j++){
                int start = i - j;
                if(f[start] == true && wordDict.contains(s.substring(start, i))){
                    f[i] = true;
                    break;
                }
            }
        }
        return f[n];
    }

    private int max(List<String> list) {
        int len = 0;
        for(String s : list){
            len = Math.max(s.length(), len);
        }
        return len;
    }
}
