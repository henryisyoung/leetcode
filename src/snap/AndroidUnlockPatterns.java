package snap;

//| 1 | 2 | 3 |
//| 4 | 5 | 6 |
//| 7 | 8 | 9 |
public class AndroidUnlockPatterns {
    public static int numberOfPatterns(int m, int n) {
        int[][] jump = new int[10][10];
        jump[1][7] = jump[7][1] = 4;
        jump[1][3] = jump[3][1] = 2;
        jump[9][3] = jump[3][9] = 6;
        jump[7][3] = jump[3][7] = 5;
        jump[9][7] = jump[7][9] = 8;
        jump[9][1] = jump[1][9] = 5;
        jump[2][8] = jump[8][2] = 5;
        jump[4][6] = jump[6][4] = 5;

        boolean[] visited = new boolean[10];
        int count = dfsSearchAll(1, m, n, jump, visited, 1, 0) * 4;
        count += dfsSearchAll(2, m, n, jump, visited, 1, 0) * 4;
        count += dfsSearchAll(5, m, n, jump, visited, 1, 0);
        return count;
    }

    private static int dfsSearchAll(int num, int m, int n, int[][] jump, boolean[] visited, int len, int count) {
        if (len >= m) count++;
        len++;
        if (len > n) return count;
        visited[num] = true;
        for (int next = 1; next <= 9; next++) {
            if (!visited[next] && (jump[num][next] == 0 || visited[jump[num][next]])) {
                count = dfsSearchAll(next, m, n, jump, visited, len, count);
            }
        }
        visited[num] = false;
        return count;
    }

    public static void main(String[] args) {
        System.out.println(numberOfPatterns(1,2));
    }
}
