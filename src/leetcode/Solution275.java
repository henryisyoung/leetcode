package leetcode;

public class Solution275 {
    public int hIndex(int[] citations) {
    	if(citations == null || citations.length == 0){
    		return 0;
    	}
        int left = 0, right = citations.length - 1, n = citations.length;
        while (left + 1< right) {
            int mid = left + (right - left)/2;
            if (citations[mid] == n - mid) return n - mid;
            else if (citations[mid] > n - mid) right = mid;
            else left = mid;
        }
        if(citations[right] == n - right) return n - right;
        return n - left;
    }
}
