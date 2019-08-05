package uber;

import leetcode.solution.TreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AutocompleteSystem {
    class TrieNode{
        TrieNode[] children;
        int times;
        String val;
        public TrieNode() {
            this.children = new TrieNode[27];
        }
    }

    class Trie{
        TrieNode root;
        public Trie() {
            this.root = new TrieNode();
        }

        public void insert(String str, int times) {
            TrieNode n = root;
            for (int i = 0; i < str.length(); i++) {
                int pos = str.charAt(i) == ' ' ? 26 : str.charAt(i) - 'a';
                if (n.children[pos] == null) {
                    n.children[pos] = new TrieNode();
                }
                n = n.children[pos];
            }
            n.val = str;
            n.times += times;
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
            List<TrieNode> result = new ArrayList<>();
            trasversTrie(n, result);
            return result;
        }

        private void trasversTrie(TrieNode n, List<TrieNode> result) {
            if (n.times > 0) {
                result.add(n);
            }
            for (int i = 0; i <= 26; i++) {
                if (n.children[i] != null) {
                    trasversTrie(n.children[i], result);
                }
            }
        }
    }
    Trie trie;
    String cur;
    public AutocompleteSystem(String[] sentences, int[] times) {
        this.trie = new Trie();
        for (int i = 0; i < times.length; i++) {
            trie.insert(sentences[i], times[i]);
        }
        this.cur = "";
    }

    public List<String> input(char c) {
        List<String> res = new ArrayList < > ();

        if (c == '#') {
            trie.insert(cur, 1);
            cur = "";
        } else {
            cur += c;
            List<TrieNode> list = trie.search(cur);
            Collections.sort(list, (a, b) -> a.times == b.times ? a.val.compareTo(b.val) : b.times - a.times);
            for (int i = 0; i < Math.min(3, list.size()); i++)
                res.add(list.get(i).val);
        }
        return res;
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
