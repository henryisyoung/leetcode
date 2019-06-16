package leetcode.solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution245 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] words = {"a","gd","c","d","d"};
		Solution245 t = new Solution245();
		System.out.println(t.shortestWordDistance(words,"a", "d"));
	}
    public int shortestWordDistance(String[] words, String word1, String word2) {
    	Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
        for(int i = 0; i < words.length; i++){
        	if(map.containsKey(words[i])){
        		List<Integer> list = map.get(words[i]);
        		list.add(i);
        		map.put(words[i], list);
        	}else{
        		List<Integer> list = new ArrayList<Integer>();
        		list.add(i);
        		map.put(words[i], list);
        	}
        }
        int min = Integer.MAX_VALUE;
        if(word1.equals(word2)){
        	List<Integer> list = map.get(word1);
        	for(int i = 0; i < list.size() - 1; i++){
        		min = Math.min(min, list.get(i + 1) - list.get(i));
        	}
        }else{
        	List<Integer> list1 = map.get(word1);
        	List<Integer> list2 = map.get(word2);
        	
            for (int a : list1) {
                for (int b : list2) {
                    min = Math.min(Math.abs(b - a), min);
                }
            }
        	
        }
        return min;
    }
    public int shortestWordDistance2(String[] words, String word1, String word2) {
    
	    int p1 = -1, p2 = -1, distance = words.length;
	    
	    for(int i = 0; i<words.length; i++){
	        if(words[i].equals(word1)){
	            p1 = i;
	            if(p1 != -1 && p2 != -1){
	                distance = (p1!=p2) ? Math.min(distance, Math.abs(p1-p2)): distance;
	            }
	        }
	        if(words[i].equals(word2)){
	            p2 = i;
	            if(p1 != -1 && p2 != -1){
	                distance = (p1!=p2) ? Math.min(distance, Math.abs(p1-p2)): distance;
	            }
	        }
	    }
	    return distance;
    }
}
