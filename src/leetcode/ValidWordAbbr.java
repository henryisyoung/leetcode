package leetcode;
import java.util.*;
public class ValidWordAbbr {
    Map<String, Set<String> > map = new HashMap<>();  
    
    public ValidWordAbbr(String[] dictionary) {  
        for(int i=0; i<dictionary.length; i++) {  
            String s = dictionary[i];  
            if(s.length() > 2 ) {  
                s = s.charAt(0) + Integer.toString(s.length()-2) + s.charAt(s.length()-1);  
            }  
            if(map.containsKey(s) ) {  
                map.get(s).add(dictionary[i]);  
            } else {  
                Set<String> set = new HashSet<String>();  
                set.add(dictionary[i]);  
                map.put(s, set);  
            }  
        }  
    }  
  
    public boolean isUnique(String word) {  
        //input check  
        String s = word;  
        if(s.length() > 2 ) {  
            s = s.charAt(0) + Integer.toString(s.length()-2) + s.charAt(s.length()-1);  
        }  
        if(!map.containsKey(s)) return true;  
        else return map.get(s).contains(word) && map.get(s).size()<=1;  
          
    }  
}
