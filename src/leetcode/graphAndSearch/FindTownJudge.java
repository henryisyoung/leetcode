package leetcode.graphAndSearch;

public class FindTownJudge {
    public int findJudge(int N, int[][] trust) {
        int[] inDegree = new int[N + 1];
        int[] toDegree = new int[N + 1];
        for (int[] edge : trust) {
            int from = edge[0], to = edge[1];
            inDegree[to]++;
            toDegree[from]++;
        }
        int judge = -1, count = 0;
        for (int i = 1; i <= N; i++ ) {
            if (toDegree[i] == 0) {
                count++;
                judge = i;
            }
        }
        if (count != 1) return -1;
        if (inDegree[judge] != N - 1) return -1;
        return judge;
    }
}
