package leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution247 {
    public List<String> findStrobogrammatic(int n) {
        return helper(n,n);
    }

	private List<String> helper(int n, int m) {
		if(m == 0){
			return new ArrayList<String>(Arrays.asList(""));
		}
		if(m == 1){
			return new ArrayList<String>(Arrays.asList("1","0","8"));
		}
		List<String> list = helper(n,m - 2);
		List<String> rlt = new ArrayList<String>();
		for(String str : list){
			if(n != m){
				rlt.add("0" + str + "0");
			}
			rlt.add("1" + str + "1");
			rlt.add("6" + str + "9");
			rlt.add("8" + str + "8");
			rlt.add("9" + str + "6");
		}
		return rlt;
	}
}
