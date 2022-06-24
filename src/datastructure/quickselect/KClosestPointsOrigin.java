package datastructure.quickselect;

public class KClosestPointsOrigin {
    public int[][] kClosest(int[][] points, int k) {
        quickSort(points, k - 1, 0, points.length - 1);
        int[][] result = new int[k][2];
        for(int i = 0; i < k; i++) result[i] = points[i];
        return result;
    }

    private void quickSort(int[][] points, int k, int l, int r) {
        if(l >= r) return;
        int pos = partition(points, l ,r);

        if(pos == k) return;
        else if(pos < k) quickSort(points, k, pos + 1, r);
        else quickSort(points, k, l, pos - 1);
    }

    private int partition(int[][] points, int l, int r) {
        int[] point = points[l];
        int pivot = cal(point);
        while(l < r) {
            while(l < r && cal(points[r]) >= pivot) r--;
            points[l] = points[r];
            while(l < r && cal(points[l]) <= pivot) l++;
            points[r] = points[l];
        }
        points[l] = point;
        return l;
    }

    private int cal(int[] point) {
        return point[0] * point[0] + point[1] * point[1];
    }
}
