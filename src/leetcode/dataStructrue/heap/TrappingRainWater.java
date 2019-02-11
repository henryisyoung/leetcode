package leetcode.dataStructrue.heap;

public class TrappingRainWater {
    public int trap(int[] height) {
        if (height == null || height.length < 2) {
            return 0;
        }
        int result = 0;
        int left = 0, right = height.length - 1;
        int leftMax = 0, rightMax = 0;
        while (left < right) {
            leftMax = Math.max(leftMax, height[left]);
            rightMax = Math.max(rightMax, height[right]);
            result += leftMax < rightMax ? leftMax - height[left++]: rightMax - height[right--];
        }
        return result;
    }
}
