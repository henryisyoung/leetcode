package discord;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchWord {
    static class TrieNode {
        Map<Character, TrieNode> children;
        boolean isWord;
        public TrieNode() {
            this.children = new HashMap<>();
            this.isWord = false;
        }
    }

    static class Trie {
        TrieNode root;
        Map<Character, List<Character>> map;
        public Trie(Map<Character, List<Character>> map) {
            this.root = new TrieNode();
            this.map = map;
        }

        public void insert(String s) {
            if (s== null || s.length() == 0) return;
            TrieNode cur = root;
            insertHelper(s, cur, 0);
        }

        private void insertHelper(String s, TrieNode cur, int index) {
            if (index == s.length()) {
                cur.isWord = true;
                return;
            }
            char c = s.charAt(index);
            if (!cur.children.containsKey(c)) {
                cur.children.put(c, new TrieNode());
                cur = cur.children.get(c);
                insertHelper(s, cur, index + 1);
            }
            if(map.containsKey(c)) {
                for (char replace : map.get(c)) {
                    if (!cur.children.containsKey(replace)) {
                        cur.children.put(replace, new TrieNode());
                        cur = cur.children.get(replace);
                        insertHelper(s, cur, index + 1);
                    }
                }
            }
        }

        public boolean search(String s) {
            if (s == null || s.length() == 0) return false;
            TrieNode cur = root;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (!cur.children.containsKey(c)) {
                    return false;
                }
                cur = cur.children.get(c);
                if (cur.isWord) {
                    return true;
                }
            }
            return cur.isWord;
        }
    }

    public static boolean hasBadWords(String s, Map<Character, List<Character>> map, List<String> badWords) {
        if (s == null || s.length() == 0) {
            return false;
        }
        Trie trie = new Trie(map);
        for (String word : badWords) {
            trie.insert(word);
        }

        for (int i = 0; i < s.length(); i++) {
            String str = s.substring(i);
            if (trie.search(str)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String s = "applefoolbanana";
        String s1 = "applefaalbanana";
        String s2 = "applef00lbanana";
        List<String> badWords = Arrays.asList("fool", "silly");
        Map<Character, List<Character>> map = new HashMap<>();
        map.put('o', Arrays.asList('0'));
        map.put('l', Arrays.asList('1', 'i'));

        System.out.println(hasBadWords(s2, map, badWords));
    }
}
