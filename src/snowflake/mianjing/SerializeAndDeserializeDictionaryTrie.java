package snowflake.mianjing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
Problem Statement
Your task is to create a codec (a tool for encoding and decoding) for a dictionary Trie.

You will receive a list of unique words written in lowercase letters. You need to build a Trie using these words and implement the following two functions:

serialize(words): Converts the Trie structure into a single string.
deserialize(data): Reconstructs the Trie from that string and returns all the words sorted in lexicographical order (alphabetical order).
You are free to design the string format however you like. The only requirement is that the reconstructed dictionary must match the original exactly.

Key Requirements
Do not use regex or complex search tools.
Remember that words can share starting letters (prefixes).
You must use standard Trie or dictionary-style nodes.
Sample Scenarios
Case 1:

Input: words = ["app","apple","bat"]

Output: ["app","apple","bat"]

Logic: After processing the list through your functions, the recovered words are exactly the same and sorted alphabetically.

Case 2:

Input: words = ["dog","deer","deal"]

Output: ["deal","deer","dog"]

Logic: The output provides the words from the rebuilt Trie, sorted alphabetically.

Operational Limits
Word Count: 0 <= words.length <= 10^4
Word Length: 1 <= words[i].length <= 50
Character Type: words[i] uses only lowercase English letters.
Uniqueness: The input words list contains unique strings.
Total Size: The sum of all word lengths is at most 2 * 10^5.
 */
/**
 * Codec for a dictionary Trie.
 *
 * Format
 * ------
 * Pre-order DFS in alphabetical order. For each node we emit:
 *   1. its character
 *   2. '#' if the node is the end of some word
 *   3. the serializations of its children (sorted)
 *   4. ']' to indicate "back up to parent"
 *
 * The root is virtual (no character of its own); we just emit each top-level
 * subtree concatenated.
 *
 * Example
 *   words = ["app","apple","bat"]
 *   trie (root has children a, b):
 *       a
 *       └─ p
 *          └─ p#         (end of "app")
 *             └─ l
 *                └─ e#   (end of "apple")
 *       b
 *       └─ a
 *          └─ t#         (end of "bat")
 *
 *   serialized = "app#le#]]]]]bat#]]]"
 *
 * Why this format
 *   - Single pass to write, single pass to read.
 *   - No escapes / no separators — the alphabet ('a'..'z'), '#', and ']' are
 *     all distinct, so the parser never has to guess.
 *   - Captures shared prefixes naturally (each prefix is written once).
 *   - No regex, no fancy parsing.
 */
public class SerializeAndDeserializeDictionaryTrie {

    private static class Node {
        TreeMap<Character, Node> children = new TreeMap<>();
        boolean isWord = false;
    }

    // ------------------------------------------------------------
    // serialize
    // ------------------------------------------------------------
    public String serialize(List<String> words) {
        Node root = buildTrie(words);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Character, Node> e : root.children.entrySet()) {
            dfs(e.getKey(), e.getValue(), sb);
        }
        return sb.toString();
    }

    private Node buildTrie(List<String> words) {
        Node root = new Node();
        if (words == null) return root;
        for (String w : words) {
            Node cur = root;
            for (int i = 0; i < w.length(); i++) {
                char c = w.charAt(i);
                cur = cur.children.computeIfAbsent(c, k -> new Node());
            }
            cur.isWord = true;
        }
        return root;
    }

    // Word length <= 50 so recursion depth is fine.
    private void dfs(char ch, Node node, StringBuilder sb) {
        sb.append(ch);
        if (node.isWord) sb.append('#');
        for (Map.Entry<Character, Node> e : node.children.entrySet()) {
            dfs(e.getKey(), e.getValue(), sb);
        }
        sb.append(']');
    }

    // ------------------------------------------------------------
    // deserialize: returns words sorted lexicographically
    // ------------------------------------------------------------
    public List<String> deserialize(String data) {
        Node root = parse(data);
        List<String> result = new ArrayList<>();
        collect(root, new StringBuilder(), result);
        return result;
    }

    private Node parse(String data) {
        Node root = new Node();
        if (data == null || data.isEmpty()) return root;

        Deque<Node> stack = new ArrayDeque<>();
        stack.push(root);
        for (int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            if (c >= 'a' && c <= 'z') {
                // New child under current top, descend into it.
                Node parent = stack.peek();
                Node child = new Node();
                parent.children.put(c, child);
                stack.push(child);
            } else if (c == '#') {
                // Mark the node we just descended into as a word end.
                stack.peek().isWord = true;
            } else if (c == ']') {
                // Done with this subtree, pop back up to its parent.
                stack.pop();
            }
            // Anything else would be an invalid format; we ignore for simplicity.
        }
        return root;
    }

    // In-order traversal of trie → words come out sorted because we use TreeMap.
    private void collect(Node node, StringBuilder path, List<String> out) {
        if (node.isWord) out.add(path.toString());
        for (Map.Entry<Character, Node> e : node.children.entrySet()) {
            path.append(e.getKey());
            collect(e.getValue(), path, out);
            path.deleteCharAt(path.length() - 1);
        }
    }

    // ============================================================
    // Demo / tests
    // ============================================================
    public static void main(String[] args) {
        SerializeAndDeserializeDictionaryTrie codec = new SerializeAndDeserializeDictionaryTrie();

        roundTrip(codec, Arrays.asList("app", "apple", "bat"));   // ["app","apple","bat"]
        roundTrip(codec, Arrays.asList("dog", "deer", "deal"));   // ["deal","deer","dog"]
        roundTrip(codec, new ArrayList<>());                       // []
        roundTrip(codec, Arrays.asList("a"));                      // ["a"]
        roundTrip(codec, Arrays.asList("a", "ab", "abc"));         // ["a","ab","abc"]  (every prefix is a word)
        roundTrip(codec, Arrays.asList("zebra", "apple", "ant", "banana", "band"));
    }

    private static void roundTrip(SerializeAndDeserializeDictionaryTrie codec, List<String> words) {
        String enc = codec.serialize(words);
        List<String> dec = codec.deserialize(enc);

        List<String> expected = new ArrayList<>(words);
        expected.sort(String::compareTo);

        boolean ok = dec.equals(expected);
        System.out.println("input:   " + words);
        System.out.println("encoded: " + enc);
        System.out.println("decoded: " + dec + (ok ? "  OK" : "  MISMATCH (expected " + expected + ")"));
        System.out.println();
    }
}
