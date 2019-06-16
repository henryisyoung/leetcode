package leetcode.solution;
import java.util.*;
public class Solution49 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution49 t = new Solution49();
		String[] strs = {"eat", "tea", "tan", "ate", "nat", "bat"};
		String[] strss ={"",""};
		List<List<String>> ls = t.groupAnagrams(strs);
		System.out.println(ls.toString());
	}

    public List<List<String>> groupAnagrams(String[] strs) {
    	if(strs==null) return null;
    	List<List<String>> ls = new ArrayList<List<String>>();
        HashMap<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
        Arrays.sort(strs);
        for(String s:strs){
        	String ns = anagrams(s);
        	if(! map.containsKey(ns)){
        		ArrayList<String> list = new ArrayList<String>();
        		list.add(s);
        		map.put(ns, list);
        	}else{
        			map.get(ns).add(s);
        	}
        }
        ls.addAll(map.values());
        return ls;
    }

	private String anagrams(String s) {
		char[] arr=s.toCharArray();
		Arrays.sort(arr);
		return new String(arr);
	}
	
	public List<List<String>> groupAnagramsFaster(String[] strs) {
	    HashMap<String, List<String>> map = new HashMap<>();
	    for (String s : strs) {
	        char[] ar = s.toCharArray();
	        Arrays.sort(ar);
	        String sorted = String.valueOf(ar);
	        List<String> list = map.get(sorted);
	        if (list == null) list = new ArrayList<String>();
	        list.add(s);
	        map.put(sorted, list);
	    }
	    List<List<String>> res = new ArrayList<>();
	    for (List<String> l : map.values()) {
	        Collections.sort(l);  // faster point than Arrays.sort(strs);
	        res.add(l);
	    }
	    return res;
	}
}
