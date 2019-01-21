package leetcode;
import java.util.*;
public class Solution249 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
    public List<List<String>> groupStrings(String[] strings) {

	    List<List<String>> result = new ArrayList<List<String>>();  
	    HashMap<String, List<String>> d = new HashMap<>();  
	    for(int i = 0; i < strings.length; i++) {  
	        StringBuffer sb = new StringBuffer();  
	        for(int j = 0; j < strings[i].length(); j++) {  
	            sb.append(Integer.toString(((strings[i].charAt(j) - strings[i].charAt(0)) + 26) % 26));  
	            sb.append(" ");  
	        }  
	        String shift = sb.toString();  
	        if(d.containsKey(shift)) {  
	            d.get(shift).add(strings[i]);  
	        } else {  
	            List<String> l = new ArrayList<>();  
	            l.add(strings[i]);  
	            d.put(shift, l);  
	        }  
	    }  
	      
	    for(String s : d.keySet()) {  
	        Collections.sort(d.get(s));  
	        result.add(d.get(s));  
	    }   
	    return result;  
    }
}
