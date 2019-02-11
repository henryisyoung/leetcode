package leetcode.twoPointer;

public class ContainerWithMostWater {
    public int maxArea(int[] height) {
        if (height == null || height.length == 0) {
            return 0;
        }
        int max = 0;
        int left = 0, right = height.length - 1;
        while (left < right) {
            max = Math.max(max, area(left, right, height));
            int leftHeight = height[left], rightHeigt = height[right];
            if (leftHeight < rightHeigt) {
                left++;
            } else {
                right--;
            }
        }
        return max;
    }

    private int area(int left, int right, int[] height) {
        return (right - left) * Math.min(height[left], height[right]);
    }
}
