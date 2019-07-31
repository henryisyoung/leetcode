package leetcode.twoPointer;

import java.util.*;

public class LongestSubstringWithAtMostKDistinctCharacters {
    public static int lengthOfLongestSubstringKDistinct(String s, int k) {
        int i = 0, n = s.length(), max = 0;
        while (i + k <= n) {
            int[] table = new int[26];
            int mask = 0 , prev = i;
            for (int j = i; j < n; j++) {
                int pos = s.charAt(j) - 'a';
                table[pos]++;
                if (table[pos] < k) mask |= (1 << pos);
                else mask &= (~(1 << pos));
                if (mask == 0) {
                    max = Math.max(max, j - i + 1);
                    prev = j;
                }
            }
            i = prev + 1;
        }
        return max;
    }

    public int longestSubstringRecursion(String s, int k) {
        if(s == null || s.length() == 0){
            return 0;
        }
        Map<Character, Integer> map = new HashMap<>();
        Set<Character> set = new HashSet<>();
        for (char c : s.toCharArray()){
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        for (Character key : map.keySet()) {
            if (map.get(key) < k) {
                set.add(key);
            }
        }
        if (set.size() == 0) return s.length();
        if (set.size() == map.size()) return 0;
        int i = 0, j = 0, max = 0;
        while (j < s.length()) {
            char c = s.charAt(j);
            if (set.contains(c)) {
                if (i != j) {
                    max = Math.max(max, longestSubstringRecursion(s.substring(i, j), k));
                }
                i = j + 1;
            }
            j++;
        }
        if(i != j){
            max = Math.max(max, longestSubstringRecursion(s.substring(i,j), k));
        }
        return max;
    }
    public int lengthOfLongestSubstringKDistinct2(String s, int k) {
        int maxLen = 0;

        // Key: letter; value: the number of occurrences.
        Map<Character, Integer> map = new HashMap<Character, Integer>();
        int i, j = 0;
        char c;
        for (i = 0; i < s.length(); i++) {
            while (j < s.length()) {
                c = s.charAt(j);
                if (map.containsKey(c)) {
                    map.put(c, map.get(c) + 1);
                } else {
                    if(map.size() ==k)
                        break;
                    map.put(c, 1);
                }
                j++;
            }

            maxLen = Math.max(maxLen, j - i);
            c = s.charAt(i);
            if(map.containsKey(c)){
                int count = map.get(c);
                if (count > 1) {
                    map.put(c, count - 1);
                } else {
                    map.remove(c);
                }
            }
        }
        return maxLen;
    }

    public static void main(String[] args) {
        String s = "weitong";
        int k = 2;
        System.out.println(lengthOfLongestSubstringKDistinct(s, k));
    }
}
