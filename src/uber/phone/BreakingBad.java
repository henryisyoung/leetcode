package uber.phone;


import java.util.Arrays;
import java.util.List;

public class BreakingBad {
    class TrieNode {
        TrieNode[] children;
        boolean isWord;
        public TrieNode() {
            this.children = new TrieNode[26];
            this.isWord = false;
        }
    }

    class Trie {
        TrieNode root;
        public Trie() {
            this.root = new TrieNode();
        }

        public void insert(String s) {
            TrieNode node = root;
            for (int i = 0; i < s.length(); i++) {
                Character c = Character.toLowerCase(s.charAt(i));
                int pos = c - 'a';
                if (node.children[pos] == null) {
                    node.children[pos] = new TrieNode();
                }
                node = node.children[pos] ;
            }
            node.isWord = true;
        }

        public int findPrefix(String s) {
            int prev = -1;
            TrieNode node = root;

            for (int i = 0; i < s.length(); i++) {
                Character c = Character.toLowerCase(s.charAt(i));
                int pos = c - 'a';
                if (node.children[pos] == null) {
                    return prev;
                }
                node = node.children[pos] ;
                if (node.isWord) {
                    prev = i;
                }
            }

            return prev;
        }
    }

    public String breakingBad(String names, List<String> symbols) {
        Trie trie = new Trie();
        for (String sym : symbols) {
            trie.insert(sym);
        }
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        String[] name = names.split(" ");
        for (String str : name) {
            int pos = trie.findPrefix(str);
            if (pos == -1) {
                sb.append(prefix + str);
            } else {
                String local = "[";
                for (int i = 0; i <= pos; i++) {
                    char c = i == 0 ? Character.toUpperCase(str.charAt(i)) : str.charAt(i);
                    local = local + c;
                }
                local = local + "]" + str.substring(pos + 1);
                sb.append(prefix + local);
            }
            prefix = " ";
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        BreakingBad bd = new BreakingBad();
        List<String> symbols = Arrays.asList("H", "He", "Li", "Be", "B", "C", "N", "F", "Ne", "Na", "Al", "Smit");
        String name = "henry alba jerry smith f b";
        System.out.println(bd.breakingBad(name, symbols));
    }
}
