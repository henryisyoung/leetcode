package snap;

import java.util.*;

public class WordAbbreviation {
    public List<String> wordsAbbreviation(List<String> dict) {
        Map<String, List<Node>> map = new HashMap<>();
        List<String> result = new ArrayList<>();
        int index = 0;
        for (String word : dict) {
            String abb = abbrev(word, 0);
            map.putIfAbsent(abb, new ArrayList<>());
            map.get(abb).add(new Node(index, word));
            index++;
        }
        String[] ans = new String[dict.size()];

        for (List<Node> group: map.values()) {
            Collections.sort(group, (a, b) -> a.word.compareTo(b.word));

            int[] lcp = new int[group.size()];
            for (int i = 1; i < group.size(); ++i) {
                int p = longestCommonPrefix(group.get(i-1).word, group.get(i).word);
                lcp[i] = p;
                lcp[i-1] = Math.max(lcp[i-1], p);
            }

            for (int i = 0; i < group.size(); ++i) {
                ans[group.get(i).index] = abbrev(group.get(i).word, lcp[i]);
            }
        }
        return Arrays.asList(ans);
    }

    public int longestCommonPrefix(String word1, String word2) {
        int i = 0;
        while (i < word1.length() && i < word2.length() && word1.charAt(i) == word2.charAt(i)) {
            i++;
        }
        return i;
    }

    public String abbrev(String word, int i) {
        int N = word.length();
        if (N - i <= 3) return word;
        return word.substring(0, i+1) + (N - i - 2) + word.charAt(N-1);
    }

    class Node{
        String word;
        int index;
        public Node(int index, String val){
            this.index = index;
            this.word = val;
        }
    }
    public List<String> wordsAbbreviation2(List<String> words) {
        Map<String, List<Node>> groups = new HashMap();
        String[] ans = new String[words.size()];

        int index = 0;
        for (String word: words) {
            String ab = abbrev(word, 0);
            if (!groups.containsKey(ab))
                groups.put(ab, new ArrayList());
            groups.get(ab).add(new Node( index, word));
            index++;
        }

        for (List<Node> group: groups.values()) {
            TrieNode trie = new TrieNode();
            for (Node iw: group) {
                TrieNode cur = trie;
                for (char letter: iw.word.substring(1).toCharArray()) {
                    if (cur.children[letter - 'a'] == null)
                        cur.children[letter - 'a'] = new TrieNode();
                    cur.count++;
                    cur = cur.children[letter - 'a'];
                }
            }

            for (Node iw: group) {
                TrieNode cur = trie;
                int i = 1;
                for (char letter: iw.word.substring(1).toCharArray()) {
                    if (cur.count == 1) break;
                    cur = cur.children[letter - 'a'];
                    i++;
                }
                ans[iw.index] = abbrev(iw.word, i-1);
            }
        }

        return Arrays.asList(ans);
    }
    class TrieNode {
        TrieNode[] children;
        int count;
        TrieNode() {
            children = new TrieNode[26];
            count = 0;
        }
    }
}
