package leetcode.graphAndSearch.backTracking;

import java.util.*;

public class GeneralizedAbbreviation {
    public List<String> generateAbbreviations(String word) {
        List<String> rlt = new ArrayList<String>();
        if(word == null || word.length() == 0){
            rlt.add("");
            return rlt;
        }
        helper(word, rlt, "", 0);
        return rlt;
    }

    private void helper(String word, List<String> rlt, String tmp, int pos){
        rlt.add(tmp + word.substring(pos));
        int n = word.length();
        if(pos == n){
            return;
        }
        int i = 0;
        if(pos != 0){
            i = pos + 1;
        }
        for(; i <= n; i++){
            String pre = tmp + word.substring(pos, i);
            for(int len = 1; len + i <= n; len++){
                helper(word, rlt, pre + Integer.toString(len), i + len);
            }
        }
    }
}
