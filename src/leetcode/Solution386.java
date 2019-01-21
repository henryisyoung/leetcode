package leetcode;
import java.util.*;
public class Solution386 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution386 t = new Solution386();
		System.out.println(t.lexicalOrderRE(121).equals(t.lexicalOrder(121)) );
	}
	
    public List<Integer> lexicalOrder(int n) {
    	List<Integer> rlt = new ArrayList<Integer>();
    	int cur = 1;
    	for(int i = 0; i < n; i++){
    		rlt.add(cur);
    		if(cur * 10 <= n){
    			cur *= 10;
    		}else{
    			if(cur >= n){
    				cur /= 10;
    			}
    			cur++;
    			while(cur % 10 == 0){
    				cur /= 10;
    			}
    		}
    	}
    	return rlt;
    }
    
    //recursive
    public List<Integer> lexicalOrderRE(int n) {
    	List<Integer> rlt = new ArrayList<Integer>();
    	for(int i = 1; i <= 9; i++){
    		helper(rlt, i, n);
    	}
    	return rlt;
    }

	private void helper(List<Integer> rlt, int cur, int n) {
		if(cur <= n){
			rlt.add(cur);
		}
		for(int i = 0; i <= 9; i++){
			if(cur * 10 + i <= n){
				helper(rlt, cur * 10 + i, n);
			}else{
				break;
			}
		}
		
	}
}
