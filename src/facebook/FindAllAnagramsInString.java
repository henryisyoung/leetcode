package facebook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindAllAnagramsInString {
    public static List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = new ArrayList<>();
        if (s == null || s.length() < p.length()) return result;
        int[] pTable = new int[26];
        int[] sTable = new int[26];
        for (char c : p.toCharArray()) {
            pTable[c - 'a']++;
        }
        for (int i = 0; i < p.length(); i++) {
            sTable[s.charAt(i) - 'a']++;
        }
        if (Arrays.equals(sTable, pTable)) result.add(0);
        for (int end = p.length(); end < s.length(); end++) {
            char removed = s.charAt(end - p.length());
            sTable[removed - 'a']--;
            sTable[s.charAt(end) - 'a']++;
            if (Arrays.equals(sTable, pTable)) result.add(end - p.length() + 1);
        }

        return result;
    }

    public static void main(String[] args) {
        String s = "cbaebabacd", p = "abc";
        System.out.println(findAnagrams(s, p));
    }
}
