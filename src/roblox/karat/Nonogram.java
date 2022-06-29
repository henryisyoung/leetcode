package roblox.karat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Nonogram {
    public static List<Boolean> validateNonogram(char[][] board, List<List<Integer>> rows, List<List<Integer>> cols) {
        int m = board.length, n = board[0].length;
        List<Boolean> result = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            String s = new String(board[i]);
            result.add(validHelper(s, rows.get(i)));
        }

        for (int j = 0; j < n; j++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i <m; i++) {
                sb.append(board[i][j]);
            }
            result.add(validHelper(sb.toString(), cols.get(j)));
        }

        return result;
    }

    private static Boolean validHelper(String s, List<Integer> pos) {
        int n = s.length();
        int count = 0;
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < n; i++){
            char c = s.charAt(i);
            if (c != 'B') {
                if (count > 0) {
                    list.add(count);
                }
                count = 0;
            } else {
                count++;
            }
        }
        if (count > 0) {
            list.add(count);
        }
        System.out.println(list + " " + pos + list.equals(pos));
        return list.equals(pos);
    }

    public static void main(String[] args) {
        char[][] board = {
                {'W', 'W','W','W'},
                {'B', 'W','W','W'},
                {'B', 'W','B','B'},
                {'W', 'W','B','W'},
                {'B', 'B','W','W'},
        };
        List<List<Integer>> rows = Arrays.asList(
                Arrays.asList(),
                Arrays.asList(1),
                Arrays.asList(1,2),
                Arrays.asList(1),
                Arrays.asList(2)
        );
        List<List<Integer>> cols = Arrays.asList(
                Arrays.asList(2,1),
                Arrays.asList(1),
                Arrays.asList(2),
                Arrays.asList(1)
        );
        System.out.println(validateNonogram(board, rows, cols));
    }

}
