package leetcode.solution;

public class Solution42 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
    public int trap(int[] height) {
        if(height == null || height.length == 0) return 0;
        int leftMax = 0, rightMax = 0, waterTrapped = 0, left = 0, right = height.length-1;
        while(left < right) {
            leftMax = leftMax > height[left] ? leftMax : height[left];
            rightMax = rightMax > height[right] ? rightMax : height[right];
            waterTrapped += leftMax < rightMax ? leftMax - height[left++] : rightMax - height[right--];
        }
        return waterTrapped;
    }
}
