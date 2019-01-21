package leetcode;
import java.util.*;
public class Solution293 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution293 t = new Solution293();
		String s = "----++++";
		System.out.println(t.generatePossibleNextMoves(s));
	}
    public List<String> generatePossibleNextMoves(String s) {
    	List<String> rlt = new ArrayList<String>();
    	if(s == null || s.length() == 0){
    		return rlt;
    	}
    	int n = s.length();
    	for(int i = 0; i < n - 1; i++){
    		if(s.charAt(i) == s.charAt(i + 1) && s.charAt(i) == '+'){
    			if(i < n - 2){
    				String str = s.substring(0, i) + "--" + s.substring(i + 2);
    				rlt.add(str);
    			}else{
    				String str = s.substring(0, i) + "--";
    				rlt.add(str);
    			}
    		}
    	}
    	return rlt;
    }
}
