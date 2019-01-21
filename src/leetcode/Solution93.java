package leetcode;

import java.util.*;

public class Solution93 {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "010010";
		Solution93 t = new Solution93();
		System.out.println((t.restoreIpAddresses(s)));
	}
	
    public List<String> restoreIpAddresses(String s) {
    	List<String> rlt = new ArrayList<String>();
    	if(s== null || s.length() < 4 || s.length() > 16){
    		return rlt;
    	}
    	
    	List<String> list = new ArrayList<String>();
    	helper(rlt, list, 0, s);
    	return rlt;
    }

	private void helper(List<String> rlt, List<String> list, int start, String s) {
		if(list.size() == 4){
			if(start != s.length())
                return;
			
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < 4; i++){
				String tmp = list.get(i);
				if(i < 3){
	                sb.append(tmp);
	                sb.append(".");
				}else{
					sb.append(tmp);
				}
			}
			rlt.add(sb.toString());
			return;
		}
		for(int i = start; i < s.length() && i <= start+3; i++){
			String tmp = s.substring(start, i + 1);
			if(isValid(tmp)){
				//System.out.println(tmp);
				list.add(tmp);
				helper(rlt,list,i+1,s);
				list.remove(list.size() - 1);
			}
		}
	}

	private boolean isValid(String tmp) {
	    if(tmp.charAt(0) == '0'){
	        return tmp.equals("0");
	    }
        int digit = Integer.valueOf(tmp);
        return digit >= 0 && digit <= 255;
	}
}
