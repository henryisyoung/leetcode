package leetcode;
import java.util.*;
public class Solution46 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution46 t = new Solution46();
		int[] nums = {1,2,3};
		System.out.println(t.permute3(nums));
		
	}
	 public List<List<Integer>> permute(int[] nums) {
	        List<Integer> list = new ArrayList<Integer>();
	        if(nums==null) return null;
	        for(int i : nums) list.add(i);
	        return getPermute(list);
	    }

	    private List<List<Integer>> getPermute(List<Integer> list) {
	        List<List<Integer>> ret = new ArrayList<List<Integer>>();
	        if(list.size() == 1){
	            ret.add(list);
	            return ret;
	        }
	        for(Integer i : list){
	            List<Integer> copy = new ArrayList<Integer>(list);
	            copy.remove(i);
	            List<List<Integer>> fromRec = getPermute(copy);
	            for(List<Integer> l : fromRec){
	                List<Integer> copyFromRec = new ArrayList<Integer>(l);
	                copyFromRec.add(i);
	                ret.add(copyFromRec);
	            }
	        }
	        return ret;
	    }
	    public List<List<Integer>> permuteFast(int[] nums) {
	        List<List<Integer>> list = new LinkedList<>();
	        permute(list, nums, 0);
	        return list;
	    }

	    private void permute(List<List<Integer>> list, int[] nums, int start){
	        if(start == nums.length - 1){
	            List<Integer> l = new LinkedList<>();
	            for(int n:nums)
	                l.add(n);
	            list.add(l);
	        }
	        else{
	            for(int i=start; i<nums.length; i++){
	                int tmp = nums[i];
	                nums[i] = nums[start];
	                nums[start] = tmp;
	                permute(list, nums, start+1);
	                tmp = nums[i];
	                nums[i] = nums[start];
	                nums[start] = tmp;
	            }
	        }
	    }

	    
	    public List<List<Integer>> permute3(int[] nums) {
	    	if(nums == null || nums.length == 0) return null;
	    	List<List<Integer>> rlt = new ArrayList<List<Integer>>();
	    	List<Integer> list = new ArrayList<>();
	    	//Arrays.sort(nums);
	    	permuteHelper(nums,rlt,list);
	    	return rlt;
	    }
		private void permuteHelper(int[] nums, List<List<Integer>> rlt,
				List<Integer> list) {
			
			if(list.size() == nums.length){
				rlt.add(new ArrayList<Integer>(list));
				return;
			}
			
			for(int i = 0; i < nums.length; i++){
	            if(list.contains(nums[i])){
	                continue;
	            }
				list.add(nums[i]);
				permuteHelper(nums,rlt,list);
				list.remove(list.size() - 1);
			}
		}

}
