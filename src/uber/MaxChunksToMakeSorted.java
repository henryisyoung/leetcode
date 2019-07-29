package uber;

public class MaxChunksToMakeSorted {
    public int maxChunksToSorted(int[] arr) {
        int result = 0;
        int n = arr.length, max = 0;
        for (int i = 0; i < n; i++) {
            max = Math.max(max, arr[i]);
            if (max == i) result++;
        }
        return result;
    }
}
