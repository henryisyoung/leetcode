package leetcode.solution;
import java.util.*;
public class Solution139 {
    public boolean wordBreak(String s, Set<String> wordDict) {
        int n = s.length(), maxLen = getMaxLen(wordDict);
        boolean[] f = new boolean[n + 1];
        f[0] = true;
        for(int i = 1; i <= n; i++){
        	for(int lastLen = 1; lastLen <= maxLen && lastLen <= i; lastLen++){
        		int j = i - lastLen;
        		String str = s.substring(j, i); 
        		if(f[j] && wordDict.contains(str)){
        			f[i] = true;
        			break;
        		}
        	}
        }
        return f[n];
    }

	private int getMaxLen(Set<String> wordDict) {
		int max = Integer.MIN_VALUE;
		for(String s:wordDict){
			max = Math.max(max, s.length());
		}
		return max;
	}
}
