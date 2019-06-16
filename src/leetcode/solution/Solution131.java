package leetcode.solution;
import java.util.*;
public class Solution131 {
    public List<List<String>> partition(String s) {
    	List<List<String>> rlt = new ArrayList<>();
    	if(s.length() == 0 || s == null) return rlt;
    	List<String> list = new ArrayList<>();
    	partitionHelper(s,rlt,list,0);
    	return rlt;
    }

	private void partitionHelper(String s, List<List<String>> rlt,
			List<String> list, int pos) {
		if(pos == s.length()){
			rlt.add(new ArrayList<>(list));
			return;
		}
		for(int i = pos + 1; i <= s.length(); i++){
			String str = s.substring(pos,i);
			if(isPal(str)){
				list.add(str);
				partitionHelper(s,rlt,list,i);
				list.remove(list.size() - 1);
			}
		}
	}

	private boolean isPal(String str) {
		int start = 0, end = str.length() - 1;
		while(start < end){
			if(str.charAt(start) != str.charAt(end)){
				return false;
			}
			start++;
			end--;
		}
		return true;
	}
}
