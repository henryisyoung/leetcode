package rippling;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
// https://www.1point3acres.com/bbs/thread-871348-1-1.html
public class Canvas {

    Stack[][] board;
    int rows, cols;
    Map<Character, int[]> map;
    public Canvas(int rows, int cols) {
        this.map = new HashMap<>();
        this.board = new Stack[rows][cols];
        this.cols = cols;
        this.rows = rows;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Stack<Character> stack = new Stack<>();;
                stack.add('-');
                board[i][j] = stack;
            }
        }
    }

    public void insert(char a, int r, int c, int w, int h) {
        for (int i = r; i < Math.min(rows, r + h); i++) {
            for (int j = c; j < Math.min(cols, r + w); j++) {
                Stack stack = board[i][j];
                stack.add(a);
            }
        }
        map.put(a, new int[]{r, c, w, h});
    }

    public void move(char a, int nr, int nc) {
        if (!map.containsKey(a)) {
            return;
        }
        int[] nums = map.get(a);
        int r = nums[0], c = nums[1], h = nums[2], w = nums[3];
        remove(a, r,c,h,w);
        insert(a, nr,nc, h, w);
    }

    private void remove(char a, int r, int c, int h, int w) {
        for (int i = r; i < Math.min(rows, r + h); i++) {
            for (int j = c; j < Math.min(cols, r + w); j++) {
                Stack stack = board[i][j];
                stack.remove((Character) a);
            }
        }
        map.remove(a);
    }

    public void print() {
        for (Stack[] chars : board) {
            StringBuilder sb = new StringBuilder();
            String prefix = "";
            for (Stack stack : chars) {
                sb.append(prefix + stack.peek());
                prefix = " ";
            }
            System.out.println(sb.toString());
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Canvas canvas = new Canvas(15,15);
        canvas.insert('a', 2,2,4,4);
        canvas.insert('c', 2,2,4,4);
        canvas.print();

        canvas.insert('d', 2,2,6,6);
        canvas.insert('5', 5,5,6,6);
        canvas.print();

        canvas.move('c',13,13);
        canvas.move('d',3,3);
        canvas.print();
    }
}
