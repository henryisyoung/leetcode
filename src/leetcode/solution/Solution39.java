package leetcode.solution;

import java.util.*;

public class Solution39 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution39 t = new Solution39();
		int[] a = {2,3,6,7};
		System.out.println(t.combinationSum2(a, 7));
		ArrayList<Integer> list = new ArrayList<Integer>();
		//list.add(152);
		//list.remove((Object) 152);
		//System.out.println(list.toString());
		
	}
	public List<List<Integer>> combinationSum(int[] candidates, int target) {
		List<List<Integer>> lists = new ArrayList<List<Integer>>();
		 if(candidates==null) return lists;
		 Arrays.sort(candidates);
		 ArrayList<Integer> list = new ArrayList<Integer>();
		 combinationSumHelper(candidates,target,0,list,0,lists);
		 return lists;
	 }
	
	private void combinationSumHelper(int[] arr, int target, int sum,
			ArrayList<Integer> list, int index, List<List<Integer>> lists) {
		if(sum==target){	
			lists.add( new ArrayList<Integer>(list));
			return;
		}
		if(sum>target){
			return;
		}
		for(int i=index;i<arr.length;i++){
			sum+=arr[i];
			list.add(arr[i]);
			combinationSumHelper(arr,target,sum,list,i,lists);
			sum-=arr[i];
			//list.remove((Object) arr[i]);  // influence speed a lot
			list.remove(list.size()-1);
		}
	}
	
	public List<List<Integer>> combinationSum2(int[] candidates, int target) {
	    Arrays.sort(candidates);
	    List<List<Integer>> sets = new ArrayList<List<Integer>>();
	    backTrack(sets,new ArrayList<Integer>(),candidates,0,target,candidates.length-1);
	    return sets;
	}
	public void backTrack(List<List<Integer>> sets,List<Integer> path,int[] nums,int sum,int target,int index){
	    if(sum==target){
	        sets.add(new ArrayList<Integer>(path));
	        return;
	    }
	    if(sum>target){
	        return;
	    }
	    for(int i=index;i>=0;i--){
	        sum += nums[i];
	        path.add(0,nums[i]);
	        backTrack(sets,path,nums,sum,target,i);
	        sum -= nums[i];
	        path.remove(0);
	    }
	}
	public List<List<Integer>> combinationSum3(int[] candidates, int target) {
	    Arrays.sort(candidates);
	    List<List<Integer>> sets = new ArrayList<List<Integer>>();
	    backTrack3(sets,new ArrayList<Integer>(),candidates,0,target,0);
	    return sets;
	}
	public void backTrack3(List<List<Integer>> sets,List<Integer> path,int[] nums,int sum,int target,int index){
	    if(sum==target){
	        sets.add(new ArrayList<Integer>(path));
	        return;
	    }
	    for(int i=index;i<nums.length;i++){
	        if(sum + nums[i]>target){
	            break;
	        }
	        path.add(nums[i]);
	        backTrack3(sets,path,nums,sum + nums[i],target,i);
	        path.remove(path.size()-1);
	    }
	}
}
