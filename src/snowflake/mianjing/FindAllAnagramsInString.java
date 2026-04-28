package snowflake.mianjing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindAllAnagramsInString {
    public List<Integer> findPos(String s, String p) {
        List<Integer> rlt = new ArrayList<>();
        if (s.length() < p.length()) return rlt;
        int[] pCount = new int[26];
        for (char c : p.toCharArray()) pCount[c - 'a']++;
        int m = s.length(), n = p.length();
        int[] sCount = new int[26];
        for (int i = 0; i < n; i++) {
            sCount[s.charAt(i) - 'a']++;
        }
        if (Arrays.equals(sCount, pCount)) {
            rlt.add(0);
        }
        int i = n;
        while (i < m) {
            int newIndex = s.charAt(i) - 'a';
            sCount[newIndex]++;
            int lastIndex = s.charAt(i - n) - 'a';
            sCount[lastIndex]--;
            if (Arrays.equals(sCount, pCount)) {
                rlt.add(i - n + 1);
            }
            i++;
        }
        return rlt;
    }
}
