package google.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NumberMatchingSubsequences {
    public static void main(String[] args) {
        String source = "abcd", target = "c";
    }

    //https://leetcode.com/problems/number-of-matching-subsequences/
    static class Node {
        int index;
        String s;
        public Node(String s, int index) {
            this.index = index;
            this.s = s;
        }
    }
    public static int numMatchingSubseq(String S, String[] words) {
        List<Node>[] nodes = new ArrayList[26];
        Arrays.fill(nodes, new ArrayList<>());
        for (String word : words) {
            int pos = word.charAt(0) - 'a';
            nodes[pos] = new ArrayList<>();
            nodes[pos].add(new Node(word, 0));
        }
        int count = 0;
        for (char c : S.toCharArray()) {
            int pos = c - 'a';
            List<Node> list = nodes[pos];
            nodes[pos] = new ArrayList<>();

            for (Node node : list) {
                node.index++;
                if (node.index == node.s.length()) {
                    count++;
                } else {
                    int nextPos = node.s.charAt(node.index) - 'a';
                    nodes[nextPos].add(node);
                }
            }
            list.clear();
        }
        return count;
    }
}
