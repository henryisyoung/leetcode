package leetcode.graphAndSearch.backTracking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StickersToSpellWord {
    int min = Integer.MAX_VALUE;
    public int minStickers(String[] stickers, String target) {
        List<String> list = new ArrayList<>();
        char[] source = new char[26];
        for (String word : stickers) {
            for (char c : word.toCharArray()) {
                source[c - 'a']++;
            }
        }
        char[] targetArr = buildArray(target);
        if (!cover(source, targetArr)) {
            return -1;
        }
        source = new char[26];
        dfsSearchAll(list, targetArr, stickers, 0, source);
        return min;
    }

    private void dfsSearchAll(List<String> list, char[] targetArr, String[] stickers, int pos, char[] source) {
        if (cover(source, targetArr)) {
            min = Math.min(list.size(), min);
            return;
        }
        for (int i = pos; i < stickers.length; i++) {
            String cur = stickers[i];

        }
    }

    private boolean cover(char[] source, char[] target) {
        for (int i = 0; i < 26; i++) {
            if (target[i] > source[i]) {
                return false;
            }
        }
        return true;
    }

    private char[] buildArray(String word) {
        char[] array = new char[26];
        for (char c : word.toCharArray()) {
            array[c - 'a']++;
        }
        return array;
    }
}
