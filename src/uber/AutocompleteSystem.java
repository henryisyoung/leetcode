package uber;

import java.util.*;

public class AutocompleteSystem {
    class TrieNode{
        TrieNode[] children;
        int times;
        String sentence;
        public TrieNode() {
            this.children = new TrieNode[27];
            this.times = 0;
            this.sentence = "";
        }
    }

    class Trie{
        TrieNode root;
        public Trie() {
            this.root = new TrieNode();
        }

        public void insert(String str, int time) {
            TrieNode n = root;
            for (int i = 0; i < str.length(); i++) {
                int pos = str.charAt(i) == ' ' ? 26 : str.charAt(i) - 'a';
                if (n.children[pos] == null) {
                    n.children[pos] = new TrieNode();
                }
                n = n.children[pos];
            }
            n.sentence = str;
            n.times += time;
        }

        public List<TrieNode> search(String str) {
            TrieNode n = root;
            for (int i = 0; i < str.length(); i++) {
                int pos = str.charAt(i) == ' ' ? 26 : str.charAt(i) - 'a';
                if (n.children[pos] == null) {
                    return new ArrayList<>();
                }
                n = n.children[pos];
            }
            List<TrieNode> list = new ArrayList<>();
            traverse(n, list);
            return list;
        }

        private void traverse(TrieNode n, List<TrieNode> list) {
            if (n.times > 0) {
                list.add(n);
            }
            for (int pos = 0; pos < 27; pos++) {
                if (n.children[pos] == null) continue;
                traverse(n.children[pos], list);
            }
        }
    }

    Trie trie;
    String cur;
    public AutocompleteSystem(String[] sentences, int[] times) {
        this.trie = new Trie();
        for (int i = 0; i < sentences.length; i++) {
            trie.insert(sentences[i], times[i]);
        }
        this.cur = "";
    }

    public List<String> input(char c) {
        if (c == '#') {
            trie.insert(cur, 1);
            cur = "";
            return new ArrayList<>();
        }
        cur += c;
        List<TrieNode> list = trie.search(cur);
        Collections.sort(list, new Comparator<TrieNode>() {
            @Override
            public int compare(TrieNode o1, TrieNode o2) {
                if (o1.times == o2.times) {
                    return o1.sentence.compareTo(o2.sentence);
                }
                return o2.times - o1.times;
            }
        });
        List<String> result = new ArrayList<>();
        for (int i = 0; i < list.size() && i < 3; i++) {
            result.add(list.get(i).sentence);
        }
        return result;
    }

    public static void main(String[] args) {
        String[] sentences = {"i love you","island","iroman","i love leetcode"};
        int[] times = {5,3,2,2};
        AutocompleteSystem solver = new AutocompleteSystem(sentences, times);
        List<String> result  = solver.input('i');
        List<String> result4  = solver.input(" ".charAt(0));
        List<String> result2  = solver.input('a');
        List<String> result3  = solver.input('#');
        System.out.println(result);
        System.out.println(result4);
        System.out.println(result2);
        System.out.println(result3);
    }
}
