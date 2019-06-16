package leetcode.solution;

public class Solution300 {
    public int lengthOfLIS(int[] nums) {
    	if(nums==null || nums.length==0) return 0;
        int n = nums.length;
        int[] f = new int[n];
        f[0] = 1;
        for(int i = 1; i < n;i++){
        	f[i] = 1;
        	for(int j = 0; j < i; j++){
        		if(nums[j] <= nums[i] && f[i] < f[j] + 1){
        			f[i] = f[j] + 1;
        		}
        	}
        }
        return findMax(f);
    }

	private int findMax(int[] f) {
		int max = f[0];
		for(int i : f){
			if(i > max){
				max = i;
			}
		}
		return max;
	}
	
    static int CeilIndex(int A[], int l, int r, int key)
    {
        while (r - l > 1)
        {
            int m = l + (r - l)/2;
            if (A[m]>=key)
                r = m;
            else
                l = m;
        }
 
        return r;
    }
 
    static int LongestIncreasingSubsequenceLength(int A[], int size)
    {
        // Add boundary case, when array size is one
    	if(A == null || A.length == 0) return 0;
        int[] tailTable   = new int[size];
        int len; // always points empty slot
 
        tailTable[0] = A[0];
        len = 1;
        for (int i = 1; i < size; i++)
        {
            if (A[i] < tailTable[0])
                // new smallest value
                tailTable[0] = A[i];
 
            else if (A[i] > tailTable[len-1])
                // A[i] wants to extend largest subsequence
                tailTable[len++] = A[i];
 
            else
                // A[i] wants to be current end candidate of an existing
                // subsequence. It will replace ceil value in tailTable
                tailTable[CeilIndex(tailTable, -1, len-1, A[i])] = A[i];
        }
 
        return len;
    }
}
