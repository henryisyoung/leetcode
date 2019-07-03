package leetcode.trie;

import java.util.*;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

public class CamelcaseMatching {
    class TrieNode {
        Character val;
        TrieNode child;

        TrieNode(Character val) {
            this.val = val;
        }
    }

    private void constructTrie(String pattern, TrieNode root) {
        TrieNode currNode = root;
        for (int i = 0; i < pattern.length(); i++) {
            currNode.child = new TrieNode(pattern.charAt(i));
            currNode = currNode.child;
        }
    }

    public List<Boolean> camelMatch(String[] queries, String pattern) {
        List<Boolean> res = new ArrayList<>();
        TrieNode root = new TrieNode('#');
        constructTrie(pattern, root);
        for (int j = 0; j < queries.length; j++) {
            String query = queries[j];
            TrieNode currNode = root.child;
            for (int i = 0; i < query.length(); i++) {
                char key = query.charAt(i);
                if (currNode == null) {
                    if (isUpperCase(key)) {
                        res.add(false);
                        break;
                    }
                } else {
                    if (isUpperCase(currNode.val) && isUpperCase(key)) { // upper, upper
                        if (currNode.val != key) {
                            res.add(false);
                            break;
                        } else {
                            currNode = currNode.child;
                        }
                    } else if (isLowerCase(currNode.val) && isUpperCase(key)) { // lower, upper
                        res.add(false);
                        break;
                    } else if (currNode.val == key) { // upper, lower or lower, lower
                        currNode = currNode.child;
                    }
                }
            }
            if (res.size() == j)
                res.add(currNode == null);
        }
        return res;
    }

    public static void main(String[] args) {
        String[] queries = {"FooBar","FooBarTest","FootBall","FrameBuffer","ForceFeedBack"};
        String pattern = "FoBa";
        CamelcaseMatching solver = new CamelcaseMatching();
        List<Boolean> result = solver.camelMatch(queries, pattern);
        System.out.println(result.toString());
    }
}
