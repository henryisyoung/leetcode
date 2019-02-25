package airbnb;

import java.util.ArrayList;
import java.util.List;

public class KEditDistance {
    private void search(String curr, String target, int k, TrieNode root, int[] prevDist, List<String> result) {
        if (root.isLeaf) {
            if (prevDist[target.length()] <= k) {
                result.add(curr);
            } else {
                return;
            }
        }
        for (int i = 0; i < 26; i++) {
            if (root.children[i] == null) {
                continue;
            }
            int[] currDist = new int[target.length() + 1];
            currDist[0] = curr.length() + 1;
            for (int j = 1; j <= target.length(); j++) {
                if (target.charAt(j - 1) == (char) (i + 'a')) {
                    currDist[j] = prevDist[j - 1];
                } else {
                    /* 如果dp[i][j]表示当前trie树所构成的prefix字符串的前i个字符和target字符串的 前j个字符的编辑距离的话，
                    按照以前的做法，如果当前第i个和第j个字符不相同的话，则有目前的对应关系:
                    dp[i-1][j-1] + 1 replace =>prevDp[j-1]+1
                    dp[i][j - 1] + 1 insert => dp[j - 1] + 1 dp[i - 1][j] + 1
                    delete => prevDp[j] + 1
                    */
                    currDist[j] = Math.min(Math.min(prevDist[j - 1], prevDist[j]), currDist[j - 1]) + 1;
                }
            }
            search(curr + (char) (i + 'a'), target, k, root.children[i], currDist, result);
        }
    }

    public List<String> getKEditDistance(String[] words, String target, int k) {
        List<String> res = new ArrayList<>();
        if (words == null || words.length == 0 || target == null || target.length() == 0 || k < 0) {
            return res;
        }
        Trie trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }
        TrieNode root = trie.root;
        // The edit distance from curr to target
        int[] prev = new int[target.length() + 1];
        for (int i = 0; i < prev.length; i++) {
            prev[i] = i;
        }
        search("", target, k, root, prev, res);
        return res;
    }

    class TrieNode {
        TrieNode[] children;
        boolean isLeaf;
        public TrieNode() {
            children = new TrieNode[26];
        }
    }

    class Trie {
        TrieNode root;
        public Trie() {
            root = new TrieNode();
        }
        // Add a word into trie
        public void insert(String s) {
            if (s == null || s.length() == 0) {
                return; }
            TrieNode p = root;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (p.children[c - 'a'] == null) {
                    p.children[c - 'a'] = new TrieNode();
                }
                if (i == s.length() - 1) {
                    p.children[c - 'a'].isLeaf = true;
                }
                p = p.children[c - 'a'];
            }
        }
    }

    public List<String> getKEditDistanceDP(String[] words, String target, int k) {
        List<String> result = new ArrayList<>();
        for (String word : words) {
            if (Math.abs(word.length() - target.length()) > k) {
                continue;
            }
            if (isValidPair(word, target, k)) {
                result.add(word);
            }
        }
        return result;
    }

    private boolean isValidPair(String word, String target, int k) {
        int m = word.length(), n = target.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word.charAt(i - 1) == word.charAt(j - 1)) {
                    dp[i][j] = Math.min(Math.min(dp[i][j - 1], dp[i - 1][j]) + 1, dp[i - 1][j - 1]);
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i][j - 1], dp[i - 1][j]), dp[i - 1][j - 1]) + 1;
                }
            }
        }
        return dp[m][n] <= k;
    }

    public static void main(String[] args) {
        KEditDistance editor = new KEditDistance();
        String[] words = {"abc", "abd", "abcd", "adc"};
        String target = "ac";
        int k = 2;
        List<String> list = editor.getKEditDistanceDP(words, target, k);
        System.out.println(list.toString());
    }
}
