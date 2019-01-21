package leetcode;
import java.util.*;
public class Solution290 {
	public static void main(String[] args) {
		Solution290 t = new Solution290();
		System.out.println(t.wordPattern("abba", "dog cat cat fish"));
	}
	
    public boolean wordPattern(String pattern, String str) {
    	char[] patternLetters = pattern.toCharArray();
    	String[] words = str.split(" ");
    	Map<Character, String> mappings = new HashMap<Character, String>();

        if(patternLetters.length != words.length) return false;

        for(int i = 0; i < patternLetters.length; i++)
        {
            if(mappings.get(patternLetters[i]) != null && !mappings.get(patternLetters[i]).equals(words[i]))
                return false; 
            else if(mappings.get(patternLetters[i]) == null && mappings.containsValue(words[i]))
                return false; 
            else
                mappings.put(patternLetters[i], words[i]); 
        }
        
        return true;
    }
}
