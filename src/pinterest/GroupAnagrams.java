package pinterest;

import leetcode.ListNode;

import java.util.*;

public class GroupAnagrams {
    public List<List<String>> groupAnagrams(String[] strs) {
        List<List<String>> result = new ArrayList<>();
        Map<String, List<String>> map = new HashMap<>();
        for (String str : strs) {
            int[] count = new int[26];
            for (char c : str.toCharArray()) {
                count[c - 'a']++;
            }
            String key = buildCount(count);
            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>());
            }
            map.get(key).add(str);
        }
        for (String key : map.keySet()) {
            result.add(map.get(key));
        }
        return result;
    }

    private String buildCount(int[] count) {
        StringBuilder sb = new StringBuilder();
        for (int i : count) {
            sb.append(i + "#");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String[] strs = {"eat","tea","tan","ate","nat","bat"};
        GroupAnagrams solver = new GroupAnagrams();
        List<List<String>> result = solver.groupAnagrams(strs);
        System.out.println(result.toString());
    }
}
