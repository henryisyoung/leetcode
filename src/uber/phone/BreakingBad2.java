package uber.phone;


import java.util.Arrays;
import java.util.List;

public class BreakingBad2 {

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
                int pos = Character.toLowerCase(s.charAt(i)) - 'a';
                if (node.children[pos] == null) {
                    node.children[pos] = new TrieNode();
                }
                node = node.children[pos];
            }
            node.isWord = true;
        }

        public int findPrefix(String s) {
            int pos = -1;
            TrieNode node = root;
            for (int i = 0; i < s.length(); i++) {
                int index = Character.toLowerCase(s.charAt(i)) - 'a';
                if (node.children[index] == null) {
                    return pos;
                }
                node = node.children[index];
                if (node.isWord) {
                    pos = i;
                }
            }
            return pos;
        }
    }

    public String breakingBad(String names, List<String> symbols) {
        Trie trie = new Trie();
        for (String sym : symbols) {
            trie.insert(sym);
        }
        StringBuilder sb = new StringBuilder();
        String[] nameArray = names.split(" ");
        String prefix = "";
        for (String name : nameArray) {
            System.out.println(name);
            int pos = trie.findPrefix(name);
            if (pos == -1) {
                sb.append(prefix + name);
            } else {
                StringBuilder local = new StringBuilder();
                local.append("[");
                for (int i = 0; i <= pos; i++) {
                    char curChar = i == 0 ? Character.toUpperCase(name.charAt(i)) : name.charAt(i);
                    local.append(curChar);
                }
                local.append("]");
                local.append(name.substring(pos + 1));
                sb.append(prefix + local);
            }
            prefix = " ";
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        BreakingBad2 bd = new BreakingBad2();
        List<String> symbols = Arrays.asList("H", "He", "Li", "Be", "B", "C", "N", "F", "Ne", "Na", "Al");
        String name = "henry alba jerry smith f b";
        System.out.println(bd.breakingBad(name, symbols));
    }
}
