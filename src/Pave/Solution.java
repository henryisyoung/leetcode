package Pave;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Solution {
    public static void main(String[] args) {
        test();
    }

    public static void test() {
        Map<Character, Integer> map = new HashMap<>();
        map.put('A', 9);
        map.put('E', 8);
        map.put('C', 4);
        map.put('B', 2);
        map.put('F', 1);
        map.put('D', 5);
        Solution hTree = new Solution(map);
        System.out.println(hTree.decode("110101011").equals("ABC"));
        System.out.println(hTree.decode("111111").equals("AAA"));
        System.out.println(hTree.decode("10").equals("E"));
        System.out.println(hTree.decode("10000100").equals("EDF"));
        System.out.println(hTree.decode("100001100").equals("")); // true

//        System.out.println(hTree.encode("A").equals("0")); // true
//        System.out.println(hTree.encode("ABC").equals("110101011")); // true
//        System.out.println(hTree.encode("ABC").equals("11010101")); // false
//        System.out.println(hTree.encode("AAA").equals("111111")); // true
//        System.out.println(hTree.encode("E").equals("10")); // true
//        System.out.println(hTree.encode("EDF").equals("10000100")); // true
//
//        try {
//            System.out.println(hTree.encode("X").equals("10000100")); // exception
//        } catch (IllegalStateException e) {
//            System.out.println(e.getMessage());
//        }
    }

    class Node {
        Node left, right;
        int freq;
        char val;
        boolean isLeaf;

        public Node(int freq, char val, boolean isLeaf) {
            this.freq = freq;
            this.val = val;
            this.isLeaf = isLeaf;
        }

        public Node(int freq, boolean isLeaf) {
            this.freq = freq;
            this.isLeaf = isLeaf;
        }
    }

    Node root;
    Map<Character, String> charPath;
    Map<String, Character> pathChar;
    public Solution(Map<Character, Integer> map) {
        this.root = buildTree(map);
        this.charPath = new HashMap<>();
        this.pathChar = new HashMap<>();
        // if map size is one that the root path is 0 as default;
        buildCharPath();
        System.out.println(charPath);
        System.out.println(pathChar);
    }

    private void buildCharPath() {
        String path = "";
        Node cur = root;
        dfsFindPath(path, cur);
    }

    // n node in total O(n)
    // space logn space
    private void dfsFindPath(String path, Node cur) {
        if (cur.isLeaf) {
            char curChar = cur.val;
            String localPath = path.equals("") ? "0" : path;
            charPath.put(curChar, localPath);
            pathChar.put(localPath, curChar);
            return;
        }
        dfsFindPath(path + "0", cur.left);
        dfsFindPath(path + "1", cur.right);
    }

    // n * logn tine
    // space is n
    private Node buildTree(Map<Character, Integer> map) {
        if (map.isEmpty()) {
            return null;
        }
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> (a.freq - b.freq));
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            pq.add(new Node(entry.getValue(), entry.getKey(), true));
        }

        while (pq.size() > 1) {
            Node leftNode = pq.poll();
            Node rightNode = pq.poll();
            Node node = new Node(leftNode.freq + rightNode.freq, false);
            node.left = leftNode;
            node.right = rightNode;
            pq.add(node);
        }
        return pq.poll();
    }


    // ABC - > a str concate a encode path + b encode path + c encode path
    // s len is k time is O(k)
    public String encode(String s) {
        StringBuilder sb = new StringBuilder();
        for (char cur : s.toCharArray()) {
            if (!charPath.containsKey(cur)) {
                throw new IllegalStateException("Not a valid input string");
            }
            String path = charPath.get(cur);
            sb.append(path);
        }
        return sb.toString();
    }

    // k is str len O(k)
    public String decode(String str) {
        int l = 0;
        StringBuilder sb = new StringBuilder();
        for (int r = 1; r <= str.length(); r++) {
            String subStr = str.substring(l, r);
            if (pathChar.containsKey(subStr)) {
                sb.append(pathChar.get(subStr));
                if (r != str.length()) {
                    l = r;
                }
            }
        }
        return sb.toString();
    }

}
