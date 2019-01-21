package leetcode;
import java.util.*;
public class Solution271 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public String encode(List<String> strs) {
		StringBuilder sb = new StringBuilder();
		for(String str : strs){
			sb.append(str.length()+"#");
			sb.append(str);
		}
		return sb.toString();
	}
	
	public List<String> decode(String s) {
		List<String> rlt = new ArrayList<String>();
		int start = 0;
		while(start < s.length()){
			int pos = s.indexOf("#", start);
			int len = Integer.parseInt(s.substring(start, pos));
			rlt.add(s.substring(pos + 1, pos + len + 1));
			start = pos + len + 1;
		}
		return rlt;
	}
}
