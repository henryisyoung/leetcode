package leetcode;

public class Question278_Binary_Search {
    public int firstBadVersion(int n) {
        int start = 0;
        int end = n;
        while(start + 1 < end){
            int mid = start + (end - start)/2;
            if (isBadVersion(mid)){
                end = mid;
            }else{
                start = mid;
            }
        }
        if(isBadVersion(start)) return start;
        return end;
    }

    private boolean isBadVersion(int mid) {
        return true;
    }
}
