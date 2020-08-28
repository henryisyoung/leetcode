package apple;

public class AndroidUnlockPatterns {
    public int numberOfPatterns(int m, int n) {
        int[][] jump = new int[10][10];
        jump[1][7] = jump[7][1] = 4;
        jump[1][3] = jump[3][1] = 2;
        jump[9][3] = jump[3][9] = 6;
        jump[7][3] = jump[3][7] = 5;
        jump[9][7] = jump[7][9] = 8;
        jump[9][1] = jump[1][9] = 5;
        jump[2][8] = jump[8][2] = 5;
        jump[4][6] = jump[6][4] = 5;
        int count = 0;
        boolean[] visited = new boolean[10];
        count += findAll(visited, 1, m, n, 0, 1, jump) * 4;

        visited = new boolean[10];
        count += findAll(visited, 2, m, n, 0, 1, jump) * 4;

        visited = new boolean[10];
        count += findAll(visited, 5, m, n, 0, 1, jump);
        return count;
    }

    private int findAll(boolean[] visited, int pos, int m, int n, int count, int len, int[][] jump) {
        if (len >= m) count++;
        if (len == n) return count;
        visited[pos] = true;
        for (int next = 1; next <= 9; next++) {
            if (!visited[next] && (jump[pos][next] == 0 || visited[jump[pos][next]])) {
                count = findAll(visited, next, m,n,count,len + 1, jump);
            }
        }
        visited[pos] = false;
        return count;
    }
}
