package airbnb;

import java.util.ArrayList;
import java.util.List;
//https://www.jiuzhang.com/qa/4572/
public class KEditDistanceII {
    public List<String> getKEditDistance(String[] words, String target, int k) {
        List<String> result = new ArrayList<>();
        int n = target.length();
        int[] prevDP = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            prevDP[i] = i;
        }
        Trie trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }
        trasverseTrie(prevDP, "", trie.root, k, target, result);
        return result;
    }

    private void trasverseTrie(int[] prevDP, String cur, TrieNode root, int k, String target, List<String> result) {
        if (root.isWord) {
            if (prevDP[target.length()] <= k) {
                result.add(cur);
            } else {
                return;
            }
        }
        int[] dp = new int[target.length() + 1];
        dp[0] = prevDP[0] + 1;
        for (int i = 0; i < 26; i++) {
            if (root.children[i] == null) {
                continue;
            }
            for (int j = 1; j <= target.length(); j++) {
                if (target.charAt(j - 1) == (char) i + 'a') {
                    dp[j] = prevDP[j - 1];
                } else {
                    dp[j] = Math.min(Math.min(prevDP[j], dp[j - 1]), prevDP[j - 1]) + 1;
                }
            }
            trasverseTrie(dp, cur + (char) (i + 'a'), root.children[i], k, target, result);
        }
    }

    private class Trie {
        TrieNode root;
        public Trie () {
            this.root = new TrieNode();
        }

        public void insert(String s) {
            if (s == null || s.length() == 0) {
                return;
            }
            TrieNode node = root;
            for (int i = 0; i < s.length(); i++) {
                int pos = s.charAt(i) - 'a';
                if (node.children[pos] == null) {
                    node.children[pos] = new TrieNode();
                }
                node = node.children[pos];
            }
            node.isWord = true;
        }
    }

    private class TrieNode {
        TrieNode[] children;
        boolean isWord;
        public TrieNode () {
            this.children = new TrieNode[26];
            isWord = false;
        }
    }

    public static void main(String[] args) {
        KEditDistanceII editor = new KEditDistanceII();
        String[] words = {"abc", "abd", "abcd", "adc","abcde","a"};
        String target = "ac";
        int k = 2;
//        List<String> list = editor.getKEditDistanceDP(words, target, k);
        List<String> list2 = editor.getKEditDistance(words, target, k);
//        System.out.println(list.toString());
        System.out.println(list2.toString());
    }
}
