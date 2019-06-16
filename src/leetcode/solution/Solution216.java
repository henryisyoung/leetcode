package leetcode.solution;
import java.util.*;
public class Solution216 {

	public static void main(String[] args) {
		Solution216 t = new Solution216();
		System.out.println(t.combinationSum3(3, 9));

	}
    public List<List<Integer>> combinationSum3(int k, int n) {
    	List<List<Integer>> rlt = new ArrayList<>();
    	List<Integer> list = new ArrayList<Integer>();
    	helper(1, 0, k, n, rlt, list);
    	return rlt;
    }
	private void helper(int start, int sum, int k, int n,
			List<List<Integer>> rlt, List<Integer> list) {
		if(list.size() == k && sum == n){
			rlt.add(new ArrayList<Integer>(list));
			return;
		}
		for(int i = start; i <= 9; i++){
			if(sum + i > n){
				break;
			}
			list.add(i);
			helper(i + 1, sum + i, k, n, rlt, list);
			list.remove(list.size() - 1);
		}
	}
}
