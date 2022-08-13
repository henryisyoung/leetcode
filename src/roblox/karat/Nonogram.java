package roblox.karat;

import java.util.*;

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
//        char[][] board = {
//                {'W', 'W','W','W'},
//                {'B', 'W','W','W'},
//                {'B', 'W','B','B'},
//                {'W', 'W','B','W'},
//                {'B', 'B','W','W'},
//        };
//        List<List<Integer>> rows = Arrays.asList(
//                Arrays.asList(),
//                Arrays.asList(1),
//                Arrays.asList(1,2),
//                Arrays.asList(1),
//                Arrays.asList(2)
//        );
//        List<List<Integer>> cols = Arrays.asList(
//                Arrays.asList(2,1),
//                Arrays.asList(1),
//                Arrays.asList(2),
//                Arrays.asList(1)
//        );
//        System.out.println(validateNonogram(board, rows, cols));


        Map<Character, Integer> map = new HashMap<>();
        Map<Character, Integer> map2 = new HashMap<>();
        map.put('c', 1);
        map2.put('c', 1);
        map.put('a', 1);
        map2.put('a', 1);
        map.put('f', 1);
        Set<Character> s1 = new HashSet<>();
        Set<Character> s2 = map2.keySet();
        s1.removeAll(s2);
        System.out.println(s1);
        System.out.println(map + " " + map2);
    }

}
