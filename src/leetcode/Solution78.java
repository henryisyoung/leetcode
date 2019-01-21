package leetcode;
import java.util.*;
public class Solution78 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution78 t = new Solution78();
		int[] nums = {1,2,3};
		System.out.println(t.subsets(nums));
	}
    public List<List<Integer>> subsets(int[] nums) {
        if(nums==null||nums.length==0) return null;
        Arrays.sort(nums);
        return subsetsHelper(nums,nums.length);
    }
	private List<List<Integer>> subsetsHelper(int[] nums, int n) {
		List<List<Integer>> ls = new ArrayList<List<Integer>>();
		if(n==0){
			ls.add(new ArrayList<Integer>());
			return ls;
		}else{
			int val = nums[n-1];
			List<List<Integer>> nls = subsetsHelper(nums,n-1);
			ls.addAll(nls);
			for(List<Integer> list : nls){
				List<Integer> nl = new ArrayList<Integer>(list);
				nl.add(val);
				ls.add(nl);
			}
			return ls;
		}
	}
	
	public List<List<Integer>> subsetsDFS(int[] nums) {
		if(nums == null || nums.length == 0) return null;
		List<List<Integer>> rlt = new ArrayList<List<Integer>>();
		List<Integer> list = new ArrayList<Integer>();
		Arrays.sort(nums); 
		subsetsDFSHelper(nums, rlt, list, 0);
		return rlt;
	}
	
	private void subsetsDFSHelper(int[] nums,
			List<List<Integer>> rlt, List<Integer> list, int pos) {
		
		rlt.add(new ArrayList<Integer>(list));

		for(int i = pos; i < nums.length; i++){
			list.add(nums[i]);
			subsetsDFSHelper(nums, rlt, list, i + 1);
			list.remove(list.size() - 1);
		}
	}
}
