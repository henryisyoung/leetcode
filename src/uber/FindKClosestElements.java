package uber;

import java.util.*;

public class FindKClosestElements {
    public List<Integer> findClosestElements(int[] arr, int k, int x) {
        List<Integer> result = new ArrayList<>();
        if (arr == null || k > arr.length) return result;
        int pos = findPos(arr, x);
        System.out.println(pos);
        result.add(arr[pos]);
        k--;
        int i = pos - 1, j = pos + 1;
        while (k > 0 && (i >= 0 || j < arr.length)) {
            k--;
            if (i >= 0 && j < arr.length) {
                int left = arr[i], right = arr[j];
                if (Math.abs(x - left) <= Math.abs(x - right)){
                    result.add(0, arr[i--]);
                } else {
                    result.add(result.size(), arr[j++]);
                }
            } else if (i >= 0) {
                result.add(0, arr[i--]);
            } else {
                result.add(result.size(), arr[j++]);
            }
        }
//        Collections.sort(result);
        return result;
    }

    private int findPos(int[] arr, int x) {
        int left = 0, right = arr.length - 1;
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == x) {
                return mid;
            } else if (arr[mid] > x) {
                right = mid;
            } else {
                left = mid;
            }
        }
        if (arr[left] == x) return left;
        if (arr[right] == x) return right;
        return Math.abs(arr[left] - x) < Math.abs(arr[right] - x) ? left : right;
    }

    public static void main(String[] args) {
        int[] arr = {1,2,3,4,5};
        int[] arr2 = {1,2,2,2,5,5,5,8,9,9};
        int[] arr3 = {0,0,1,2,3,3,4,7,7,8};
        int k = 4, x = 3;
        int k2 =4, x2 =-1;
        int k3 =4, x3 = 0;
        int k4 =3, x4 = 5;
        FindKClosestElements solution = new FindKClosestElements();
//        List<Integer> result = solution.findClosestElements(arr, k , x);
//        List<Integer> result2 = solution.findClosestElements(arr, k2 , x2);
//        List<Integer> result3 = solution.findClosestElements(arr2, k3 , x3);
        List<Integer> result4 = solution.findClosestElements(arr3, k4 , x4);
        System.out.println(result4.toString());
    }
}
