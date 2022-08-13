package google.vo;

public class RemoveAllOnesWithRowColumnFlips {
    /*
    这道题是有关flipping的，题目给出一个01矩阵，每次允许的操作是翻转一行或者一列，问是否能通过若干次翻转使得整个矩阵都变成0。
    首先对于flipping的题目，有一个很重要的性质就是，如果某一个位置翻转了两次，那么它的值就和原来的是一样的。
    所以对于某一行或者某一列来说，要么不翻转，要么只翻转一次，因为翻转多于一次是多余的操作。
    基于这个特点，我们可以先处理第一行，对于第一行来说，如果有存在1，就把该列翻转，这样的目的是把这个1翻转成0，至于该列中其他行我先不管。
    处理完之后，说明我不需要再做列的翻转了，因为现在第一行全部都是0，如果对任意一列再做翻转，那么第一行就不满足了，所以我只需要看行翻转。

    因为只能做行翻转，我只需要看看这一行有多少个1就可以了。如果全都是1，那么我翻转这行就可以了，如果全部为0，什么也不做。\
    除了这两种情况之外，其他条件都是不满足的，可以直接return false。
     */
    public boolean removeOnes(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0] == null || grid[0].length == 0) {
            return true;
        }
        int rows = grid.length, cols = grid[0].length;
        for (int i = 0; i < cols; i++) {
            if (grid[0][i] == 1) {
                flipCol(i, grid);
            }
        }

        for (int[] array : grid) {
            if (!isSame(array)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSame(int[] array) {
        int sum = 0;
        for (int i : array) {
            sum += i;
        }
        return sum == 0 || sum == array.length;
    }

    private void flipCol(int c, int[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            grid[i][c] = grid[i][c] == 1 ? 0 : 1;
        }
    }
}
