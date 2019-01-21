package leetcode;

import java.util.*;

public class Solution30 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution30 t = new Solution30();
		String s = "barfoothefoobarman";
		String[] words = {"foo", "bar"};
		System.out.println(t.findSubstring2(s, words));
	}
	
    public List<Integer> findSubstring(String s, String[] words) {
    	HashSet<String> set = new HashSet<String>();
    	List<Integer> list = new ArrayList<Integer>();
    	int size = words[0].length(), len = size*words.length;
    	if(s==""||words==null||s.length()<len) return list;
    	for(int i=0;i<=s.length()-len;i++){
    		String str = s.substring(i,i+size); 
    		ArrayList<String> l = new ArrayList<String>(Arrays.asList(words));
    		if(isInsideArr(str,l)){
    			int j =0;
    			String ns = s.substring(i,i+len);
    			if(set.contains(ns)){
    				list.add(i);
    			}
    			while(j+size<=len){
    				if(! isInsideArr(ns.substring(j,j+size),l)){
    					break;
    				}
    				l.remove(ns.substring(j,j+size));
    				j = j+size;
    			}
    			if(j==len&&l.size()==0){
    				list.add(i);
    				set.add(ns);
    			}
    			
    		}
    	}
    	return list;
    }

	private boolean isInsideArr(String str, ArrayList<String> l) {	
		return l.contains(str);
	}
	
	public List<Integer> findSubstring2(String s, String[] words) {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        if (s==null || s.length()==0 || words==null || words.length==0) return ret;
        int wordL=words[0].length();
        if (s.length()<(wordL*words.length)) return ret;


        HashMap<String, Integer> wordId = new HashMap<String,Integer>();
        int[] wordCount = new int[words.length];
        int distinctCount=0;
        for (int i=0;i<words.length;i++){
            if (wordId.containsKey(words[i])){
                int idx = wordId.get(words[i]);
                wordCount[idx]++;
            }else{
                wordId.put(words[i],distinctCount);
                wordCount[distinctCount++]=1;
                
            }
        }

        int[][] windows = new int[wordL][distinctCount];
        int[] matchCount = new int[wordL];
        for (int i=0;i<=s.length()-wordL;i++){
            String subS = s.substring(i,i+wordL);
            if (wordId.containsKey(subS)){
                int idx = wordId.get(subS);
                if (windows[i%wordL][idx]>=wordCount[idx]) {
                	
                    for (int j=i-matchCount[i%wordL]*wordL;j<i;j+=wordL){
                        String sRemove=s.substring(j,j+wordL);
                        int idxRemove = wordId.get(sRemove);
                        windows[i%wordL][idxRemove]--;
                        matchCount[i%wordL]--;
                        if (idxRemove==idx) break;
                    }
                }
                
                windows[i%wordL][idx]++;
                //System.out.println(Arrays.toString(windows[1]));
                
                if (++matchCount[i%wordL]==words.length) ret.add(i-(matchCount[i%wordL]-1)*wordL);
                

            }else {
                 for (int j=i-matchCount[i%wordL]*wordL;j<i;j+=wordL){
                	System.out.println(j);
                    String sRemove=s.substring(j,j+wordL);
                    int idxRemove = wordId.get(sRemove);
                    windows[i%wordL][idxRemove]--;
                    matchCount[i%wordL]--;
                }
            }

        }
        return ret;
    }
	public List<Integer> findSubstring3(String s, String[] words) {
	    ArrayList<Integer> result = new ArrayList<Integer>();
	    if(s==null||s.length()==0||words==null||words.length==0){
	        return result;
	    } 
	 
	    //frequency of words
	    HashMap<String, Integer> map = new HashMap<String, Integer>();
	    for(String w: words){
	        if(map.containsKey(w)){
	            map.put(w, map.get(w)+1);
	        }else{
	            map.put(w, 1);
	        }
	    }
	 
	    int len = words[0].length();
	 
	    for(int j=0; j<len; j++){
	        HashMap<String, Integer> currentMap = new HashMap<String, Integer>();
	        int start = j;//start index of start
	        int count = 0;//count totoal qualified words so far
	 
	        for(int i=j; i<=s.length()-len; i=i+len){
	            String sub = s.substring(i, i+len);
	            if(map.containsKey(sub)){
	                //set frequency in current map
	                if(currentMap.containsKey(sub)){
	                    currentMap.put(sub, currentMap.get(sub)+1);
	                }else{
	                    currentMap.put(sub, 1);
	                }
	 
	                count++;
	 
	                while(currentMap.get(sub)>map.get(sub)){
	                    String left = s.substring(start, start+len);
	                    currentMap.put(left, currentMap.get(left)-1);
	 
	                    count--;
	                    start = start + len;
	                }
	 
	 
	                if(count==words.length){
	                    result.add(start); //add to result
	 
	                    //shift right and reset currentMap, count & start point         
	                    String left = s.substring(start, start+len);
	                    currentMap.put(left, currentMap.get(left)-1);
	                    count--;
	                    start = start + len;
	                }
	            }else{
	                currentMap.clear();
	                start = i+len;
	                count = 0;
	            }
	        }
	    }
	 
	    return result;
	}
	
	public List<Integer> findSubstring4(String s, String[] words) {
	    int N = s.length();
	    List<Integer> indexes = new ArrayList<Integer>(s.length());
	    if (words.length == 0) {
	        return indexes;
	    }
	    int M = words[0].length();
	    if (N < M * words.length) {
	        return indexes;
	    }
	    int last = N - M + 1;

	    //map each string in words array to some index and compute target counters
	    Map<String, Integer> mapping = new HashMap<String, Integer>(words.length);
	    int [][] table = new int[2][words.length];
	    int failures = 0, index = 0;
	    for (int i = 0; i < words.length; ++i) {
	        Integer mapped = mapping.get(words[i]);
	        if (mapped == null) {
	            ++failures;
	            mapping.put(words[i], index);
	            mapped = index++;
	        }
	        ++table[0][mapped];
	    }

	    //find all occurrences at string S and map them to their current integer, -1 means no such string is in words array
	    int [] smapping = new int[last];
	    for (int i = 0; i < last; ++i) {
	        String section = s.substring(i, i + M);
	        Integer mapped = mapping.get(section);
	        if (mapped == null) {
	            smapping[i] = -1;
	        } else {
	            smapping[i] = mapped;
	        }
	    }

	    //fix the number of linear scans
	    for (int i = 0; i < M; ++i) {
	        //reset scan variables
	        int currentFailures = failures; //number of current mismatches
	        int left = i, right = i;
	        Arrays.fill(table[1], 0);
	        //here, simple solve the minimum-window-substring problem
	        while (right < last) {
	            while (currentFailures > 0 && right < last) {
	                int target = smapping[right];
	                if (target != -1 && ++table[1][target] == table[0][target]) {
	                    --currentFailures;
	                }
	                right += M;
	            }
	            while (currentFailures == 0 && left < right) {
	                int target = smapping[left];
	                if (target != -1 && --table[1][target] == table[0][target] - 1) {
	                    int length = right - left;
	                    //instead of checking every window, we know exactly the length we want
	                    if ((length / M) ==  words.length) {
	                        indexes.add(left);
	                    }
	                    ++currentFailures;
	                }
	                left += M;
	            }
	        }

	    }
	    return indexes;
	}
}
