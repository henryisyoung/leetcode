package leetcode.solution;
import java.util.*;
public class Solution40 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution40 t = new Solution40();
		int[] a ={10,1,2,7,6,1,5};
		int i=0;
		//System.out.println(i--);
		System.out.println(t.combinationSum2(a, 8));
		
	}
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
    	List<List<Integer>> lists = new ArrayList<List<Integer>>();
    	if(candidates==null || candidates.length==0) return lists;
    	Arrays.sort(candidates);
    	combinationSum2Helper(candidates,target,0,0,new ArrayList<Integer>(),lists);
    	return lists;
    }
	private void combinationSum2Helper(int[] candidates, int target, int index,int sum,
			ArrayList<Integer> list, List<List<Integer>> lists) {
		if(sum==target){
			//System.out.println(sum);
			lists.add(new ArrayList<Integer>(list));
			return;
		}else{
			for(int i=index;i<candidates.length;i++){
				if(sum+candidates[i]>target) break;
				//if(i!=index && candidates[i]==candidates[i-1]) continue;
				list.add(candidates[i]);
				combinationSum2Helper(candidates,target,i+1,sum+candidates[i],list,lists);
				list.remove(list.size()-1);
			}
		}
	}
    
    

}
