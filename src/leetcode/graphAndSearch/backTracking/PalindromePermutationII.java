package leetcode.graphAndSearch.backTracking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PalindromePermutationII {
    public List<String> generatePalindromes(String s) {
        List<String> result = new ArrayList<>();
        int[] table = new int[256];
        for (char c : s.toCharArray()) {
            table[c]++;
        }

        int count = 0;
        String cur = "";
        for (int c = 0; c < 256; c++) {
            if (table[c] % 2 == 1) {
                count++;
                cur += (char) c;
                table[c]--;
                if (count > 1) return result;
            }
        }

        dfsSearchAll(result, cur, s, table);
        return result;
    }

    private void dfsSearchAll(List<String> result, String cur, String s, int[] table) {
        if (cur.length() == s.length()) {
            result.add(cur);
            return;
        }
        for (int i = 0; i < 256; i++) {
            if (table[i] > 0) {
                table[i] -= 2;
                dfsSearchAll(result, (char) i + cur + (char) i, s, table);
                table[i] += 2;
            }
        }
    }

    public static void main(String[] args) {
        PalindromePermutationII solution = new PalindromePermutationII();
        String s  = "aabb";
        List<String> result = solution.generatePalindromes(s);
        System.out.println(result.toString());
    }
}
