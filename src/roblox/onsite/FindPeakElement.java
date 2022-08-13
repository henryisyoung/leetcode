package roblox.onsite;

import java.util.ArrayList;
import java.util.List;

public class FindPeakElement {
    public int findPeakElement(int[] nums) {
        int l = 0, r = nums.length - 1;
        while (l + 1 < r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] >= nums[mid - 1] && nums[mid] >= nums[mid + 1]) {
                return mid;
            }
            else if (nums[mid] >= nums[mid - 1] && nums[mid] <= nums[mid + 1]) {
                l = mid;
            } else {
                r = mid;
            }
        }
        if (nums[l] > nums[r]) {
            return l;
        }
        return r;
    }

    public static List<Integer> findPeaks(int[] array) {
        List<Integer> result = new ArrayList<>();
        int left = Integer.MIN_VALUE, n = array.length;
        for (int i = 0; i < n; i++) {
            int right = i == n - 1 ? Integer.MIN_VALUE : array[i + 1];
            int cur = array[i];
            if (cur > Math.max(left, right)) {
                result.add(cur);
            }
            left = cur;
        }
        return result;
    }

    public static List<Integer> findPeaksPlateau(int[] array) {
        List<int[]> plateauArray = new ArrayList<>();
        int i = 0, j = 0, n = array.length;
        while (i < n) {
            int val = array[i];
            while (j < n && array[j] == val) {
                j++;
            }
            int count = j - i;
            plateauArray.add(new int[]{val, count});
            i = j;
        }
        List<Integer> result = new ArrayList<>();

        int left = Integer.MIN_VALUE;
        for (i = 0; i < plateauArray.size(); i++) {
            int right = i == plateauArray.size() - 1 ? Integer.MIN_VALUE : plateauArray.get(i + 1)[0];
            int[] cur = plateauArray.get(i);
            int count = cur[1];
            if (cur[0] > Math.max(left, right)) {
                while (count-- > 0) {
                    result.add(cur[0]);
                }
            }
            left = cur[0];
        }
        return result;
    }
    public static void main(String[] args) {
        int[] array = { 1, 4, 3, -1, 5, 5, 7, 4, 8};
        int[] array2 = {4,4,4,2,5,5,5,2,8,8};
        System.out.println(findPeaks(array));
        System.out.println(findPeaksPlateau(array2));
    }
}
