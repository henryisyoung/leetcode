package google.vo.mianjing;

import java.util.Random;

public class PlaceRandomGem {
    /*
    有K 种不同的宝石， 每个无限多， 把这些宝石放在一个 M*N 的格板上，
    要求不能有三个连续的格子是一样的宝石。 写一个函数随机生成一种放置方式。
     */

    public int[][] generateMaze(int m, int n, int k) {
        int[][] res = new int[m][n];
        Random rand = new Random();
        if (canMaze(res, 0, k, rand)) {
            return res;
        }
        return new int[m][n];
    }

    private boolean canMaze(int[][] res, int index, int k, Random rand) {
        int m = res.length;
        int n = res[0].length;
        if (index == m * n) {
            return true;
        }
        int i = index / n;
        int j = index % n;
        int color = 1 + rand.nextInt(k);
        while ((i >= 2 && res[i - 2][j] == color && res[i - 1][j] == color) || (j >= 2 && res[i][j - 2] == color && res[i][j - 1] == color)) {
            color = rand.nextInt(k);
        }
        res[i][j] = color;
        if (canMaze(res, index + 1, k, rand)) {
            return true;
        }
        res[i][j] = 0;
        return false;
    }
}
