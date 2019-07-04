package leetcode.trie;


import java.util.Arrays;
import java.util.List;

public class ReplaceWords {
    class TrieNode {
        TrieNode[] children;
        boolean isWord;
        String word;
        public TrieNode() {
            this.children = new TrieNode[26];
            this.isWord = false;
            this.word = "";
        }
    }

    class Trie{
        TrieNode root;
        public Trie() {
            this.root = new TrieNode();
        }

        public void insert(String s) {
            TrieNode node = root;
            for (int i = 0;i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (node.children[pos] == null) {
                    node.children[pos] = new TrieNode();
                }
                node = node.children[pos];
            }
            node.isWord = true;
            node.word = s;
        }

        public String search(String s) {
            TrieNode node = root;
            for (int i = 0; i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (node.children[pos] == null) {
                    return "";
                }
                if (node.children[pos].isWord) {
                    return node.children[pos].word;
                }
                node = node.children[pos];
            }
            return node.isWord ? node.word : "";
        }
    }

    public String replaceWords(List<String> dict, String sentence) {
        StringBuilder sb = new StringBuilder();
        Trie trie = new Trie();
        for (String str : dict) {
            trie.insert(str);
        }
        for (String word : sentence.split(" ")) {
            String val = trie.search(word);
//            System.out.println(val);
            if (val.equals("")) {
                sb.append(word);
            } else {
                sb.append(val);
            }
            sb.append(" ");
        }
        String result  = sb.toString();

        return result.substring(0, result.length() - 1);
    }

    public static void main(String[] args) {
        List<String> dict = Arrays.asList("cat", "bat", "rat");
        String sentence = "the cattle was rattled by the battery";
        ReplaceWords sovler = new ReplaceWords();
        System.out.println(sovler.replaceWords(dict, sentence));
    }
}
