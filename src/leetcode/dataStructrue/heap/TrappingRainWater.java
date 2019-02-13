package leetcode.dataStructrue.heap;

public class TrappingRainWater {
    public int trap(int[] height) {
        if (height == null || height.length < 2) {
            return 0;
        }
        int result = 0;
        int left = 0, right = height.length - 1;
        int leftBar = height[left], rightBar = height[right];
        while (left < right) {
            leftBar = Math.max(height[left], leftBar);
            rightBar = Math.max(height[right], rightBar);
            result += leftBar < rightBar ? leftBar - height[left++] : rightBar - height[right--];
        }
        return result;
    }
}
