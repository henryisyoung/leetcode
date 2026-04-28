package snowflake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindAllAnagramsString {
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = new ArrayList<>();
        if(s.length() < p.length()) return result;

        int[] pTable = new int[26], sTable = new int[26];

        for (char c : p.toCharArray()) {
            int index = c - 'a';
            pTable[index]++;
        }

        for (int i = 0; i < p.length(); i++) {
            int index = s.charAt(i) - 'a';
            sTable[index]++;
        }

        if (Arrays.equals(sTable, pTable)) result.add(0);

        for (int i = p.length(); i < s.length(); i++) {
            char addChar = s.charAt(i);
            sTable[addChar - 'a']++;
            char removeChar = s.charAt(i - p.length());
            sTable[removeChar - 'a']--;
            if (Arrays.equals(sTable, pTable)) result.add(i - p.length() + 1);
        }

        return result;
    }
}
