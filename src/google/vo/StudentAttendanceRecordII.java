package google.vo;

public class StudentAttendanceRecordII {

    // https://www.cnblogs.com/grandyang/p/6866756.html
    /*
    定义了三个 DP 数组 P, L, A，其中 P[i] 表示数组 [0,i] 范围内以P结尾的所有排列方式，
    L[i] 表示数组 [0,i] 范围内以L结尾的所有排列方式，A[i] 表示数组 [0,i] 范围内以A结尾的所有排列方式。
    那么最终所求的就是 P[n-1] + L[n-1] + A[n-1] 了，难点就是分别求出 P, L, A 数组的递推公式了。
     */
    public int checkRecord(int n) {
        return -1;
    }
}
