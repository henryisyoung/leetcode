package leetcode;
import java.util.*;
import java.util.Map.Entry;
public class Solution187 {

	public static void main(String[] args) {
		Solution187 t = new Solution187();
		String s = "AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT";
		System.out.println(t.findRepeatedDnaSequences(s));

	}
    public List<String> findRepeatedDnaSequences(String s) {
    	List<String> rlt = new ArrayList<String>();
    	if(s == null || s.length() < 10){
    		return rlt;
    	}
    	HashMap<String, Integer> map = new HashMap<String, Integer>();
    	for(int i = 0; i < 10; i++){
    		for(int j = i; j <= s.length() - 10; j += 10){
    			String str = s.substring(j, j + 10);
    			if(! map.containsKey(str)){
    				map.put(str, 1);
    			}else{
                    if(map.get(str) == 1){
                    	rlt.add(str);
                    }
    				map.put(str, map.get(str) + 1);
    			}
    		}
    	}
    	return rlt;
    }
}
