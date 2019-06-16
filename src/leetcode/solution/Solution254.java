package leetcode.solution;
import java.util.*;
public class Solution254 {

	public static void main(String[] args) {
		Solution254 t =  new Solution254();
		System.out.println(t.getFactors(32));

	}
    public List<List<Integer>> getFactors(int n) {
    	List<List<Integer>> rlt = new ArrayList<>();
    	List<Integer> list = new ArrayList<Integer>();
    	helper(rlt, list, n, 2);
    	return rlt;
    }
	private void helper(List<List<Integer>> rlt, List<Integer> list, int n,
			int start) {
		if(n == 1){
			if(list.size() > 1){
				rlt.add(new ArrayList<Integer>(list));
			}
			return;
		}
		for(int i = start; i <= n; i++){
			if(n % i == 0){
				list.add(i);
				helper(rlt, list, n/i, i);
				list.remove(list.size() - 1);
			}
		}
	}
}
