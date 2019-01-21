package leetcode;

public class Solution26 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution26 t = new Solution26();
		int[] a = {1,2};
		System.out.println(t.removeDuplicates(a));
	}
	
	public int removeDuplicates(int[] nums) {
		if(nums.length<2) return nums.length;
		int index = 0;
		for(int i=0;i<nums.length-1;i++){
			if(nums[i]==nums[i+1]) continue;
			else{
				nums[index] = nums[i];
				index++;
			}
		}
		return index;
	}
	

}
