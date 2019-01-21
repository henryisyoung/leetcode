package leetcode;
import java.util.*;

public class Solution159 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "ecccccccceba";
		Solution159 t = new Solution159();
		System.out.println(t.lengthOfLongestSubstringKDistinct(s, 3));
	}
    public int lengthOfLongestSubstringTwoDistinct(String s) {
        if(s == null || s.length() == 0){
        	return 0;
        }
        int start = 0, max = 0, n = s.length();
        Map<Character, Integer> map = new HashMap<Character, Integer>();
        for(int i = 0; i < n; i++){
        	char c = s.charAt(i);
        	if(map.containsKey(c)){
        		map.put(c, map.get(c) + 1);
        	}else{
        		map.put(c, 1);
        	}
        	max = Math.max(max, i - start);
        	while(map.size() > 2){
        		char p = s.charAt(start);
        		int v = map.get(p);
        		if(v > 1){
        			map.put(p, v - 1);
        		}else{
        			map.remove(p);
        		}
        		start++;
        	}
        }
        max = Math.max(max, s.length()-start);
        return max;
    }
    
    public int lengthOfLongestSubstringKDistinct(String s, int k) {
        if(s == null || s.length() == 0){
        	return 0;
        }
        int start = 0, max = 0, n = s.length();
        Map<Character, Integer> map = new HashMap<Character, Integer>();
        for(int i = 0; i < n; i++){
        	char c = s.charAt(i);
        	if(map.containsKey(c)){
        		map.put(c, map.get(c) + 1);
        	}else{
        		map.put(c, 1);
        	}
        	max = Math.max(max, i - start);
        	while(map.size() > k){
        		char p = s.charAt(start);
        		int v = map.get(p);
        		if(v > 1){
        			map.put(p, v - 1);
        		}else{
        			map.remove(p);
        		}
        		start++;
        	}
        }
        max = Math.max(max, s.length()-start);
        return max;
    }
}
