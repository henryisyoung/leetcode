package facebook;

import java.util.Arrays;

public class KClosestPointsToOrigin {
    public static int[][] kClosest(int[][] points, int K) {
        quickSort(K - 1, points, 0, points.length - 1);
        int[][] result = new int[K][2];

        System.out.println(Arrays.deepToString(points));

        for(int i = 0; i < K; i++) {
            result[i] = points[i];
        }
        return result;
    }

    private static void quickSort(int k, int[][] list, int left, int right) {
        if(left == right) return;
        int pos = partition(list, left, right);
        if(pos == k) return;
        else if (pos < k)  quickSort(k, list, pos + 1, right);
        else quickSort(k, list, left, pos - 1);
    }

    private static int partition(int[][] list, int left, int right) {
        int l = left, r = right;
        int pivot = cal(list[left]);
        int[] arr = list[left];
        while(l < r) {
            while(l < r && cal(list[r]) >= pivot) r--;
            list[l] = list[r];
            while(l < r && cal(list[l]) <= pivot) l++;
            list[r] = list[l];
        }
        list[l] = arr;
        return l;
    }


    private static int cal(int[] a) {
        return a[0] * a[0] + a[1] * a[1];
    }

    public static void main(String[] args) {
        int[][] points = {{3,3},{5,-1},{-2,4}};
        System.out.println(kClosest(points, 3));
    }
}
