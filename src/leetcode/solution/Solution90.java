package leetcode.solution;
import java.util.*;
public class Solution90 {
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        if(nums == null || nums.length == 0) return null;
        List<List<Integer>> rlt = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        Arrays.sort(nums);
        subsetsWithDupHelper(nums,rlt,list,0);
        return rlt;
    }

	private void subsetsWithDupHelper(int[] nums,
			List<List<Integer>> rlt, List<Integer> list, int pos) {
		rlt.add(new ArrayList<Integer>(list));
		
		for(int i = pos; i < nums.length; i++){
			if(i != pos && nums[i] == nums [i - 1]){
				continue;
			}
			list.add(nums[i]);
			 subsetsWithDupHelper(nums,rlt,list,i + 1);
			 list.remove(list.size() - 1);
		}
	}
}
