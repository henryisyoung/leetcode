package rippling;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

// https://www.1point3acres.com/bbs/thread-871348-1-1.html
public class Canvas2 {

    Stack[][] board;
    int rows, cols;
    Map<Character, int[]> map;
    public Canvas2(int rows, int cols) {
        this.board = new Stack[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Stack<Character> stack = new Stack<Character>();;
                stack.add('-');
                board[i][j] = stack;
            }
        }
        this.map = new HashMap<>();
        this.rows = rows;
        this.cols = cols;
    }

    public void insert(char a, int r, int c, int w, int h) {
        for (int i = r; i < Math.min(rows, r + h); i++) {
            for (int j = c; j < Math.min(cols, c + w); j++) {
                board[i][j].add(a);
            }
        }
        map.put(a, new int[]{r, c, w, h});
    }

    public void move(char a, int nr, int nc) {
        if (!map.containsKey(a)) {
            return;
        }
        int[] array = map.get(a);
        remove(a, array);
        int w = array[2], h = array[3];
        insert(a, nr, nc, w, h);
    }

    private void remove(char a, int[] array) {
        int r = array[0], c = array[1], w = array[2], h = array[3];
        map.remove(a);
        for (int i = r; i < Math.min(rows, r + h); i++) {
            for (int j = c; j < Math.min(cols, c + w); j++) {
                board[i][j].remove((Character) a);
            }
        }
    }

    public void print() {
        for (int i = 0; i < rows; i++) {
            StringBuilder sb = new StringBuilder();
            String prefix = "";
            for (int j = 0; j < cols; j++) {
                sb.append(prefix + board[i][j].peek());
                prefix = " ";
            }
            System.out.println(sb);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Canvas2 canvas = new Canvas2(15,15);
        canvas.insert('a', 2,2,4,4);
        canvas.insert('c', 2,2,40,1);
        canvas.print();

        canvas.insert('d', 2,2,6,6);
        canvas.insert('5', 5,5,6,6);
        canvas.print();
//
        canvas.move('c',0,0);
        canvas.move('d',3,3);
        canvas.print();
    }
}
