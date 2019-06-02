package leetcode.graphAndSearch.backTracking;

import java.util.ArrayList;
import java.util.List;

public class PalindromePermutationII {
    public List<String> generatePalindromes(String s) {
        List<String> result = new ArrayList<>();
        int[] map = new int[256];
        for (char c : s.toCharArray()) {
            map[c]++;
        }
        int count = 0;
        int oddIndex = 0;
        for (int i = 0; i < 256; i++) {
            if (map[i] % 2 == 1 && count == 0) {
                count++;
                oddIndex = i;
            } else if (map[i] % 2 == 1) {
                return result;
            }
        }
        String str = "";
        if (count == 1){
            str += (char)oddIndex;
            map[oddIndex]--;
        }
        dfsSearchAll(str, s, map, result);
        return result;
    }

    private void dfsSearchAll(String str, String s, int[] map, List<String> result) {
        if (s.length() == str.length()) {
            result.add(str);
            return;
        }
        for (int i = 0; i < 256; i++) {
            if (map[i] > 0) {
                map[i] -= 2;
                dfsSearchAll((char) i + str + (char) i, s, map, result);
                map[i] += 2;
            }
        }
    }
}
