package leetcode;

public class Solution27 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
    public int removeElement(int[] nums, int val) {
    	if(nums==null) return 0;
        int len=nums.length;
        int count=0;
        for(int i=0;i<len;i++){
        	if(nums[i]!=val){
        		nums[count++]=nums[i];
        	}
        }
        return count;
    }
}
