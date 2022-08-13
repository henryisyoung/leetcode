package roblox.onsite;

import java.util.HashMap;
import java.util.Map;

public class ChatPlayer2 {
    public static int countPlays(String s, String chat) {
        if (s.length() < chat.length()) {
            return -1;
        }
        int max = -1;
        int[] counter = new int[256];
        int count = 0;
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < chat.length(); i++) {
            map.put(chat.charAt(i), i);
        }
        for (char c : s.toCharArray()) {
            counter[c]++;
            if (!validSeq(counter, c, chat, map)) {
                return -1;
            }
            if (c == 'c') {
                count++;
                max = Math.max(max, count);
            } else if (c == 't') {
                count--;
            }
        }
        Integer preq = null;
        for (char c : chat.toCharArray()) {
            if (preq == null) {
                preq = counter[c];
            } else if (preq != counter[c]) {
                return -1;
            }
        }
        return max;
    }

    private static boolean validSeq(int[] counter, char cur, String chat, Map<Character, Integer> map) {
        int pos = map.get(cur);
        if (pos == 0) {
            return true;
        }
        char prevChar = chat.charAt(pos - 1);
        return counter[prevChar] >= counter[cur];
    }


    public static void main(String[] args) {
        System.out.println(countPlays("cchathchaat", "chat"));
    }
}
