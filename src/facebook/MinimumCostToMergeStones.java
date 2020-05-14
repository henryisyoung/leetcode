package facebook;

public class MinimumCostToMergeStones {
    public static int mergeStones(int[] stones, int k) {
        if(k != 2 && stones.length % (k - 1) != 1)return -1;

        int n = stones.length;
        int[][] dp = new int[n][n];
        boolean[][] visited = new boolean[n][n];

        int[] sum = new int[n + 1];
        for (int i = 0; i < n; i++) {
            sum[i + 1] = sum[i] + stones[i];
        }

        return memoSearch(visited, 0, n - 1, dp, sum, k);
    }

    private static int memoSearch(boolean[][] visited, int left, int right, int[][] dp, int[] sum, int k) {
        if (visited[left][right]) return dp[left][right];
        if(right - left + 1 < k){
            return 0;
        }
        if(right - left + 1 == k){
            visited[left][right] = true;
            dp[left][right] = sum[right + 1] - sum[left];
            return sum[right + 1] - sum[left];
        }

        int res=Integer.MAX_VALUE;
        for(int i = left; i < right;i++){
            int leftsize= i - left+1;
            int rightsize= right - (i+1) + 1;
            if(leftsize < k && rightsize < k)continue;
            if(!visited[left][i]){
                visited[left][i] = true;
                dp[left][i]=memoSearch(visited,left,i, dp, sum, k);
            }
            if(!visited[i+1][right]){
                visited[i+1][right]=true;
                dp[i+1][right]=memoSearch(visited,i + 1, right, dp, sum, k);
            }
            int lcost=dp[left][i];
            int rcost=dp[i+1][right];
            //merge needed
            if(getremain(leftsize, k) + getremain(rightsize, k) >= k){
                res=Math.min(res,lcost+rcost + sum[right + 1] - sum[left]);
            }else{
                res=Math.min(res,lcost+rcost);
            }
        }
        return res;
    }

    public static int getremain(int size, int k){
        while(size>=k){
            int combination=size/k;
            size=size%k;
            size+=combination;
        }
        return size;
    }

    public static void main(String[] args) {
        int[] stones = {3,2,4,1};
        System.out.println(mergeStones(stones, 2));
    }
}
