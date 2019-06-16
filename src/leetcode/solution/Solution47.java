package leetcode.solution;
import java.util.*;
public class Solution47 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution47 t = new Solution47();
		int[] nums = {-1,-1,3,-1};
		System.out.println(t.permute(nums));

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
	        HashSet<Integer> set = new HashSet<Integer>();
	        for(Integer i : list){
	            if(set.add(i)){
	                List<Integer> copy = new ArrayList<Integer>(list);
	                copy.remove(i);
	                List<List<Integer>> fromRec = getPermute(copy);
	                for(List<Integer> l : fromRec){
	                    List<Integer> copyFromRec = new ArrayList<Integer>(l);
	                    copyFromRec.add(i);
	                    ret.add(copyFromRec);
	                }
	            }
	        }
	        return ret;
	    }
	
    public List<List<Integer>> permuteUnique(int[] nums) {
    	List<List<Integer>> ls = new ArrayList<List<Integer>>();
    	if(nums==null) return ls;
    	HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
    	
    	for(int i:nums){
    		if(!map.containsKey(i)){
    			map.put(i, 1);
    		}else{
    			map.put(i, map.get(i)+1);
    		}
    	}
    	List<Integer> l = new ArrayList<Integer>();
    	permuteUniqueHelper(map,l,nums.length,ls);
    	return ls;
    }
	private void permuteUniqueHelper(HashMap<Integer, Integer> map,
			List<Integer> l, int len, List<List<Integer>> ls) {
		if(len==0){
			ls.add(l);
			return;
		}
		for(int i:map.keySet()){
			int count = map.get(i);
			if(count>0){
				map.put(i, count-1);
				List<Integer> copy = new ArrayList<Integer>(l);
				copy.add(i);
				permuteUniqueHelper(map,copy,len-1,ls);
				map.put(i, count);
			}
		}
	}
	/***
	 * 
	 * Since we only need permutations of the array, the actual "content" does not change, we could find each permutation by swapping the elements in the array.
		The idea is for each recursion level, swap the current element at 1st index with each element that comes after it (including itself). For example, permute[1,2,3]:
		At recursion level 0, current element at 1st index is 1, there are 3 possibilities: [1] + permute[2,3], [2] + permute[1,3], [3] + permute[2,1].
	Take "2+permute[1,3]" as the example at recursion level 0. At recursion level 1, current elemenet at 1st index is 1, there are 2 possibilities: [2,1] + permute[3], [2,3] + permute[1].
	... and so on.
	Let's look at another example, permute[1,2,3,4,1].
	At recursion level 0, we have [1] + permute[2,3,4,1], [2] + permute[1,3,4,1], [3] + permute[2,1,4,1], [4] + permute[2,3,1,1], [1] + permute[2,3,4,1].
	1 has already been at the 1st index of current recursion level, so the last possibility is redundant. We can use a hash set to mark which elements have been at the 1st index of current recursion level, so that if we meet the element again, we can just skip it.
	 */
	public List<List<Integer>> permuteUnique2(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        if (nums==null || nums.length==0) { return ans; }
        permute(ans, nums, 0);
        return ans;
    }

    private void permute(List<List<Integer>> ans, int[] nums, int index) {
        if (index == nums.length) { 
            List<Integer> temp = new ArrayList<>();
            for (int num: nums) { temp.add(num); }
            ans.add(temp);
            return;
        }
        Set<Integer> appeared = new HashSet<>();
        for (int i=index; i<nums.length; ++i) {
            if (appeared.add(nums[i])) {
                swap(nums, index, i);
                permute(ans, nums, index+1);
                swap(nums, index, i);
            }
        }
    }

    private void swap(int[] nums, int i, int j) {
        int save = nums[i];
        nums[i] = nums[j];
        nums[j] = save;
    }
    public List<List<Integer>> permute2(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        if (nums==null || nums.length==0) return ans; 
        List<List<Integer>> rlt = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        boolean[] isVisited = new boolean[nums.length];
        Arrays.sort(nums);
		permuteHelper(nums,isVisited,rlt,list);
        return rlt;
    }
	private void permuteHelper(int[] nums, boolean[] isVisited,
			List<List<Integer>> rlt, List<Integer> list) {
		if(list.size() == nums.length){
			rlt.add(new ArrayList<>(list));
			return;
		}
		for(int i = 0; i < nums.length; i++){
			if(isVisited[i] || (i > 0 && nums[i-1] == nums[i] && !isVisited[i - 1])) {
				continue;
			}
			isVisited[i] = true;
			list.add(nums[i]);
			permuteHelper(nums,isVisited,rlt,list);
			list.remove(list.size() - 1);
			isVisited[i] = false;
		}
	}
}
