package leetcode.solution;
public class Solution167 {
	public int[] twoSum(int[] num, int target) {
		if(num == null || num.length ==0){
			return null;
		}
		int[] rlt = new int[2];
		
		int start = 0, end = num.length -1;
		while(start + 1 < end){
			int val = num[start] + num[end];
			if(val == target){
				rlt[0] = start + 1;
				rlt[1] = end + 1;
				return rlt;
			}else if(val < target){
				start++; 
				while(num[start] == num[start - 1]){
					start++; 
				}
			}else{
				end--;
				while(num[end] == num[end + 1]){
					end--;
				}
			}
		}
		rlt[0] = start + 1;
		rlt[1] = end + 1;
		return rlt;
	}
}
