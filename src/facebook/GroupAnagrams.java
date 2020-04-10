package facebook;

import java.util.*;

public class GroupAnagrams {
    public List<List<String>> groupAnagrams(String[] strs) {
        List<List<String>> result = new ArrayList<>();
        Map<String, List<String>> map = new HashMap<>();
        for (String str : strs) {
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            map.putIfAbsent(new String(chars), new ArrayList<>());
            map.get(new String(chars)).add(str);
        }
        for (List<String> val : map.values()) result.add(val);
        return result;
    }

    public static List<List<String>> groupAnagrams2(String[] strs) {
        List<List<String>> result = new ArrayList<>();
        Map<String, List<String>> map = new HashMap<>();
        for (String str : strs) {
            String counts = buildCounts(str);
            map.putIfAbsent(counts, new ArrayList<>());
            map.get(counts).add(str);
        }
        for (List<String> val : map.values()) result.add(val);
        return result;
    }

    private static String buildCounts(String str) {
        int[] count = new int[26];
        for (char c : str.toCharArray()) count[c - 'a']++;
        return Arrays.toString(count);
    }

    public static void main(String[] args) {
        String[] strs = {"eat","tea","tan","ate","nat","bat"};
        System.out.println(groupAnagrams2(strs));
    }
}