package leetcode.binarySearch;

public class FirstBadVersion {
    public int firstBadVersion(int n) {
        int start = 0, end = n;
        while (start + 1 < end){
            int mid = start + (end - start)/2;
            if(isBadVersion(mid)){
                end = mid;
            }else {
                start = mid;
            }
        }
        if (isBadVersion(start)) return start;
        return end;
    }

    private boolean isBadVersion(int mid) {
        return true;
    }

    public int firstBadVersion2(int n) {
        if(n <= 0) {
            return 0;
        }
        int start = 1, end = n;
        while (start + 1 < end) {
            int mid = start + (end - start) / 2;
            if (isBadVersion(mid)) {
                end = mid;
            } else {
                start = mid;
            }
        }
        if (isBadVersion(start)) {
            return start;
        }
        return end;
    }
}
