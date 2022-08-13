package roblox.karat;

import java.util.*;

public class CipherAndTranspose {
    //https://www.1point3acres.com/bbs/thread-907631-1-1.html
    public static char[][] cipher(String s, int rows, int cols) {
        char[][] result = new char[rows][cols];
        if (s.length() > rows * cols) {
            return result;
        }
        int index = 0, n = s.length();
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                if (index == n) break;
                result[i][j] = s.charAt(index++);
            }
        }
        return result;
    }

    public static List<String> transpose(String s, List<String> words) {
        Map<Character, Character> map = new HashMap<>();
        Set<Character> visited = new HashSet<>();
        int index = 0;
        for (char c : s.toCharArray()) {
            if (Character.isLetter(c) && !visited.contains(Character.toUpperCase(c))) {
                char val = (char) ('A' + index++);
                char cur = Character.toUpperCase(c);
                map.put(val, cur);
                visited.add(cur);
            }
        }
        List<String> result = new ArrayList<>();
        for (String word : words) {
            StringBuilder sb = new StringBuilder();
            for (char c : word.toCharArray()) {
                if (Character.isLetter(c) && map.containsKey(Character.toUpperCase(c))) {
                    char val = map.get(Character.toUpperCase(c));
                    if (Character.isUpperCase(c)) {
                        sb.append(val);
                    } else {
                        sb.append(Character.toLowerCase(val));
                    }
                } else {
                    sb.append(c);
                }
            }
            result.add(sb.toString());
        }
        return result;
    }

    public static void main(String[] args) {
//        String s = "123456789";
//        char[][] result = cipher(s, 4,3);
//        for (char[] arr : result) {
//            System.out.println(Arrays.toString(arr));
//        }
        String key = "The quick onyx goblin, Grabbing his sword ==}-------- jumps over the 1st lazy dwarf!";
        List<String> words = Arrays.asList(
                "It was all a dream.",
                "Would you kindly?"
        );
        System.out.println(transpose(key, words));
    }
}
