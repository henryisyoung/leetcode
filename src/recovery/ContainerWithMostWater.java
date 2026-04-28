package recovery;

public class ContainerWithMostWater {
    public int maxArea(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int max = 0;
        while (left < right) {
            int leftHeight = height[left];
            int rightHeight = height[right];
            int minBar = 0;
            if (leftHeight < rightHeight) {
                minBar = leftHeight;
                left++;
            } else {
                minBar = rightHeight;
                right--;
            }
            int curVol = (right - left + 1) * minBar;
            max = Math.max(max, curVol);
        }
        return max;
    }
}
