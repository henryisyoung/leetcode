package facebook;

import java.util.*;

public class WordBreak {
    public static boolean isAlienSorted(String[] words, String order) {
        int[] index = new int[26];
        for (int i = 0; i < order.length(); i++) {
            char c = order.charAt(i);
            index[c - 'a'] = i;
        }


        for (int j = 0; j < words.length - 1; j++) {
            String a = words[j], b = words[j + 1];
            for (int i = 0; i < Math.min(a.length(), b.length()); i++) {
                if (index[a.charAt(i) - 'a'] > index[b.charAt(i) - 'a']) return false;
            }
            if (a.length() > b.length()) return false;
        }
        return true;
    }

    public List<String> wordBreak2Slow(String s, List<String> wordDict) {
        List<String> result = new ArrayList<>();
        Set<String> set = new HashSet<>();
        set.addAll(wordDict);
        findAll(result, 0, "", set, s);

        return result;
    }

    private void findAll(List<String> result, int pos, String cur, Set<String> set, String s) {
        if(pos == s.length()) {
            result.add(cur);
            return;
        }
        for(int i = pos + 1; i <= s.length(); i++) {
            String str = s.substring(pos, i);
            if(!set.contains(str)) continue;
            if(cur.equals("")) {
                findAll(result, i, str, set, s);
            } else {
                findAll(result, i, cur + " " + str, set, s);
            }
        }
    }
    public List<String> wordBreak2Memo(String s, List<String> wordDict) {
        Map<Integer, List<String>> map = new HashMap<>();
        Set<String> set = new HashSet<>(wordDict);
        return dfsSearchAllMemo(set, s, 0, map);
    }

    private List<String> dfsSearchAllMemo(Set<String> set, String s,
                                          int pos, Map<Integer, List<String>> map) {
        if (map.containsKey(pos)) return map.get(pos);
        if (pos == s.length()) {
            return Arrays.asList("");
        }
        List<String> list = new ArrayList<>();
        for (int i = pos + 1; i <= s.length(); i++) {
            String str = s.substring(pos, i);
            if (set.contains(str)) {
                List<String> nexts = dfsSearchAllMemo(set, s, i, map);
                for (String next : nexts) {
                    if (next.length() == 0) list.add(str);
                    else list.add(str + " " + next);
                }
            }
        }
        map.put(pos, list);
        return list;
    }

    public static void main(String[] args) {
        String[] words = {"word","world","row"};
        String[] words2 = {"hello","leetcode"};
        String order = "worldabcefghijkmnpqstuvxyz";
        String order2 = "hlabcdefgijkmnopqrstuvwxyz";
        System.out.println(isAlienSorted(words, order));
        System.out.println(isAlienSorted(words2, order2));
    }
}
