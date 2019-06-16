package leetcode.solution;
import java.util.*;
public class Solution336 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println("abc".substring(0,1) + "abc".substring("abc".length() - 1));
	}
	
    public List<List<Integer>> palindromePairs(String[] words) {
        if(words == null || words.length == 0) return null;
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        
        for(int i = 0; i < words.length; i++){
        	map.put(words[i], i);
        }
        
        List<List<Integer>> rlt = new ArrayList<>();
        for(int i = 0; i < words.length; i++){
        	for(int j = 0; j <= words[i].length(); j++){
        		String str1 = words[i].substring(0,j);
        		String str2 = words[i].substring(j);
        		if(isPalindrome(str1)){
        			String reverS2 = new StringBuffer(str2).reverse().toString();
        			if(map.containsKey(reverS2) && map.get(reverS2) != i){
        				List<Integer> list = new ArrayList<>();
        				list.add(map.get(reverS2));
        				list.add(i);
        				rlt.add(list);
        			}
        		}
        		if(isPalindrome(str2)){
        			String reverS1 = new StringBuffer(str1).reverse().toString();
        			if(map.containsKey(reverS1) && map.get(reverS1) != i && str2.length() > 0){
        				List<Integer> list = new ArrayList<>();
        				list.add(i);
        				list.add(map.get(reverS1));
        				rlt.add(list);
        			}
        		}
        	}
        }
        return rlt;
    }

	private boolean isPalindrome(String str) {
		int left = 0, right = str.length() - 1;
		while(left < right){
			if(str.charAt(right) != str.charAt(left)) return false;
			left++;
			right--;
		}
		return true;
	}

}
