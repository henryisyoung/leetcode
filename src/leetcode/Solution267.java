package leetcode;
import java.util.*;
public class Solution267 {
	public List<String> generatePalindromes(String s) {  
        int[] map = new int[256];  
        int min = Integer.MAX_VALUE;  
        int max = Integer.MIN_VALUE;  
        for(char c: s.toCharArray()) {  
            map[c]++;  
            min = Math.min(min, c);  
            max = Math.max(max, c);  
        }  
        int count = 0;  
        List<String> res = new ArrayList<>();  
        int oddIndex = 0;  
        for(int i=min;i<=max;i++) {  
            if(count ==0 && map[i]%2==1) {  
                oddIndex = i;  
                count++;  
            }else if(map[i]%2 == 1){  
                return res;  
            }  
        }  
        String cur = "";  
        if(count==1) {  
            cur += (char)oddIndex;  
            map[oddIndex]--;  
        }  
        dfs(map, cur, s, res);  
        return res;          
    }  
    private void dfs(int[] map, String cur, String s, List<String> res) {  
        if(cur.length()==s.length()) {  
            res.add(cur);  
            return;  
        }  
        for(int i=0;i<map.length;i++) {  
            if(map[i]>0) {  
                map[i]-=2;  
                cur = (char)i + cur + (char)i;  
                dfs(map, cur, s, res);  
                cur = cur.substring(1, cur.length()-1);  
                map[i]+=2;  
            }  
        }  
    }  
}
