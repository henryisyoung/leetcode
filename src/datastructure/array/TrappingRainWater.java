package datastructure.array;

public class TrappingRainWater {
    public int trap(int[] height) {
        int left = 0, right = height.length - 1;
        int leftBar = height[0], rightBar = height[right];
        int count = 0;
        while (left < right) {
            leftBar = Math.max(leftBar, height[left]);
            rightBar = Math.max(rightBar, height[right]);
            count += leftBar < rightBar ? leftBar - height[left++] : rightBar - height[right--];
        }
        return count;
    }
}
