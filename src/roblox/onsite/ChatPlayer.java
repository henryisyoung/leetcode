package roblox.onsite;

import java.util.HashMap;
import java.util.Map;

public class ChatPlayer {

    public static int countPlays(String s, String chat) {
        int max = 0, count = 0;
        int n = s.length();
        int[] counter = new int[256];
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            counter[c]++;
            if (!validSeq(counter, chat)) {
                return -1;
            }
            if (c == 'c') {
                count++;
                max = Math.max(max, count);
            } else if (c == 't') {
                count--;
            }
        }
        Integer val = null;
        for (char c : chat.toCharArray()) {
            if (val == null) {
                val = counter[c];
            } else {
                if (val != counter[c]) {
                    return -1;
                }
            }
        }
        return max;
    }

    private static boolean validSeq(int[] counter, String chat) {
        int min = Integer.MAX_VALUE;
        for (char c : chat.toCharArray()) {
            int val = counter[c];
            if (val > min) {
                return false;
            }
            min = val;
        }
        return true;
    }

    public static int countPlays2(String s, String chat) {
        int max = 0, count = 0;
        int n = s.length();
        int[] counter = new int[256];
        Map<Character, Integer> charToIndex = new HashMap<>();
        for (int i = 0; i < chat.length(); i++) {
            charToIndex.put(chat.charAt(i), i);
        }
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            counter[c]++;
            if (!validSeq2(counter, chat, c, charToIndex)) {
                return -1;
            }
            if (c == 'c') {
                count++;
                max = Math.max(max, count);
            } else if (c == 't') {
                count--;
            }
        }
        return max;
    }

    private static boolean validSeq2(int[] counter, String chat, char cur, Map<Character, Integer> charToIndex) {
        int pos = charToIndex.get(cur);
        if (pos == 0) {
            return true;
        }
        char prevChar = chat.charAt(pos - 1);
        return counter[prevChar] >= counter[cur];
    }

    public static void main(String[] args) {
        System.out.println(countPlays2("cchathachatt", "chat"));
//        System.out.println(countPlays("cchathachatt", "chat"));
    }
}
