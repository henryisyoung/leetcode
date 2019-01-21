package leetcode;
import java.util.*;
public class WordDistance {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] words = {"a","b","c","d","d"};
		WordDistance t = new WordDistance(words);
		System.out.println(t.shortest("a", "d"));
		//System.out.println(t.shortest("d", "a"));
		// wordDistance.shortest("anotherWord1", "anotherWord2"); ["a","b","c","d","d"],distance("a,d"),distance("d,a")
	}
	Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
    public WordDistance(String[] words) {
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
    }

    public int shortest(String word1, String word2) {
    	List<Integer> list1 = map.get(word1);
    	List<Integer> list2 = map.get(word2);
    	
    	int min = Integer.MAX_VALUE;
        for (int a : list1) {
            for (int b : list2) {
                min = Math.min(Math.abs(b - a), min);
            }
        }
    	return min;
    }
}
