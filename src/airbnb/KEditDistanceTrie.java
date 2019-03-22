package airbnb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//https://www.jiuzhang.com/qa/4572/
public class KEditDistanceTrie {
    public List<String> getKEditDistance(String[] words, String target, int k) {
        List<String> result = new ArrayList<>();
        Trie trie = new Trie();
        for (String word: words) {
            trie.insert(word);
        }
        int[] prev = new int[target.length() + 1];
        for (int i = 0; i < prev.length; i++) {
            prev[i] = i;
        }
        search(prev, result, "", trie.root, k, target);
        return result;
    }

    private void search(int[] prev, List<String> result, String cur, TrieNode root, int k, String target) {
        if (root.isWord) {
            if (prev[target.length()] <= k) {
                result.add(cur);
            } else {
                return;
            }
        }

        for (int i = 0; i < 26; i++) {
            if (root.children[i] == null) {
                continue;
            }
            int[] dp = new int[target.length() + 1];
            dp[0] = cur.length() + 1;
            System.out.println("prev:" + Arrays.toString(prev));
            System.out.println("cur:" + cur);
            for (int j = 1; j <= target.length(); j++) {
                if (target.charAt(j - 1) == (char) (i + 'a')) {
                    dp[j] = prev[j - 1];
                } else {
                    dp[j] = Math.min(Math.min(dp[j - 1], prev[j]), prev[j - 1]) + 1;
                }
            }
            System.out.println("dp:" + Arrays.toString(dp));
            System.out.println("---------");

            search(dp, result, cur + (char) (i + 'a'), root.children[i], k, target);
        }
    }

    private class Trie {
        TrieNode root;
        public Trie () {
            this.root = new TrieNode();
        }
        public void insert (String s) {
            if (s == null) {
                return;
            }
            TrieNode node = root;
            int n = s.length();
            for (int i = 0; i < n; i++) {
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
            this.isWord = false;
        }
    }

    public static void main(String[] args) {
        KEditDistanceTrie editor = new KEditDistanceTrie();
        String[] words = {"abc", "abd", "abcd", "adc","abcde","a","asdasdasdasdasd", ""};
        String target = "ac";
        int k = 2;
//        List<String> list = editor.getKEditDistanceDP(words, target, k);
        List<String> list2 = editor.getKEditDistance(words, target, k);
//        System.out.println(list.toString());
        System.out.println(list2.toString());
    }
}
