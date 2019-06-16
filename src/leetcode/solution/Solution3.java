package leetcode.solution;

import java.util.HashMap;

public class Solution3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution3 t = new Solution3();
		int a =  t.lengthOfLongestSubstring2("abcwcbat");
		System.out.println(a);
	}
	
    public int lengthOfLongestSubstring(String s) {
    	  int[] dict = new int[128];
    	  int ret=0, maxret=0, i;
    	  for(i=0;i<s.length();i++){
    	    if(dict[s.charAt(i)]==0) ret++;
    	    else{
    	      int tmp = i+1-dict[s.charAt(i)];
    	      if(ret>=tmp) ret=tmp;
    	      else ret++;
    	    }
    	    dict[s.charAt(i)]=i+1;
    	    maxret = maxret<ret?ret:maxret;
    	  }
    	  return maxret;
    }

    public int lengthOfLongestSubstring2(String s) {
    	HashMap<Character,Integer> map = new HashMap<Character,Integer>();
    	int start=0,max=0,length=0;
    	for(int i=0;i<s.length();i++){
    		char c=s.charAt(i);
    		if(! map.containsKey(c)){
    			length++;
    			map.put(c, i);
    		}else{
    			int index = map.get(c);
    			if(index<start){
        			length++;
        			map.put(c, i);
    			}else{
        			max=Math.max(length,max);
        			length=length-(index-start);
        			start=index+1;
        			map.put(c, i);
    			}
    		}
    	}
    	return Math.max(length,max);
    }
}
