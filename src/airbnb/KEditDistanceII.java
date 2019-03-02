package airbnb;

import java.util.ArrayList;
import java.util.List;
//https://www.jiuzhang.com/qa/4572/
public class KEditDistanceII {
    public List<String> getKEditDistance(String[] words, String target, int k) {
        List<String> result = new ArrayList<>();
        if (words == null || words.length == 0) {
            return result;
        }
        Trie trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }
        int[] prevDp = new int[target.length() + 1];
        searchWord(trie.root, prevDp, target, "", k, result);
        return result;
    }

    private void searchWord(TrieNode root, int[] prevDp, String target, String cur, int k, List<String> result) {
        if (root.isWord) {
            if (prevDp[target.length()] <= k) {
                result.add(cur);
            } else {
                return;
            }
        }
        int[] dp = new int[target.length() + 1];
        dp[0] = prevDp[0] + 1;
        for (int i = 0; i < 26; i++) {
            if (root.children[i] == null) {
                continue;
            }
            for (int j = 1; j <= target.length(); j++) {
                if (target.charAt(j - 1) == (char) (i + 'a')) {
                    dp[j] = prevDp[j - 1];
                } else {
                    dp[j] = Math.min(prevDp[j - 1], Math.min(dp[j - 1], prevDp[j])) + 1;
                }
            }
            searchWord(root.children[i], dp, target, cur + (char) (i + 'a'), k, result);
        }
    }

    private class Trie {
        TrieNode root;
        public Trie () {
            this.root = new TrieNode();
        }

        public void insert(String str) {
            TrieNode node = root;
            for (int i = 0; i < str.length(); i++) {
                int pos = str.charAt(i) - 'a';
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
        public TrieNode() {
            this.children = new TrieNode[26];
            this.isWord = false;
        }
    }

    public static void main(String[] args) {
        KEditDistanceII editor = new KEditDistanceII();
        String[] words = {"abc", "abd", "abcd", "adc","abcde","a","asdasdasdasdasd"};
        String target = "ac";
        int k = 2;
//        List<String> list = editor.getKEditDistanceDP(words, target, k);
        List<String> list2 = editor.getKEditDistance(words, target, k);
//        System.out.println(list.toString());
        System.out.println(list2.toString());
    }
}
