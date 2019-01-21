package leetcode;

public class Solution11 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
    public int maxArea(int[] height) {
    	int max = 0;
    	int left = 0;
    	int right=height.length-1;
    	while(left < right){
    		int leftH = height[left];
    		int rightH = height[right];
    		max = Math.max(max, area(left,right,height));
    		if(leftH > rightH){
    			while(left < right && rightH >= height[right]){
    				right--;
    			}
    			
    		}else{
    			while(left < right && leftH >= height[left]){
    				left++;
    			}
    		}
    	}
        return max;
    }

	private int area(int left, int right, int[] height) {
		return (right-left)*Math.min(height[left], height[right]);
	} 

}
