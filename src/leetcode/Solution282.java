package leetcode;
import java.util.*;
public class Solution282 {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String num = "105";
		Solution282 t= new Solution282();
		System.out.println(t.addOperators(num, 5));
	}
	
    public List<String> addOperators(String num, int target) {
    	List<String> rlt = new ArrayList<String>();
    	helper(num, rlt,  0, 0, target, "");
    	return rlt;
    }

	private void helper(String s, List<String> rlt, long curRlt, long prevNum, int t,
			String tmp) {
		if(curRlt == t && s.length() == 0){
			rlt.add(new String(tmp));
			return;
		}
		for(int i = 1; i <= s.length(); i++){
			String curStr = s.substring(0,i);
			if(curStr.length() > 1 && curStr.charAt(0) == '0'){
				return;
			}
			Long curNum = Long.parseLong(curStr);
			String ns = s.substring(i);
			if(tmp.equals("")){
				helper(ns, rlt, curNum, curNum, t, curStr);
			}else{
				//*
				helper(ns, rlt, (curRlt - prevNum) + curNum*prevNum, prevNum*curNum, t, tmp + "*" + curStr);
				//+
				helper(ns, rlt, curNum + curRlt, curNum, t, tmp + "+" + curStr);
				//-
				helper(ns, rlt, - curNum + curRlt, -curNum, t, tmp + "-" + curStr);
			}
		}
	}
}
