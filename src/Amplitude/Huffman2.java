package Amplitude;

// Huffman Coding in Java

// https://www.programiz.com/dsa/huffman-coding
import java.util.*;

// IMplementing the huffman algorithm
public class Huffman2 {
    class HuffmanNode {
        HuffmanNode left, right;
        char val;
        int freq;
        boolean isLeaf;
        public HuffmanNode(char val , int freq) {
            this.freq = freq;
            this.val = val;
            this.isLeaf = true;
        }
        public HuffmanNode() {
            this.freq = 0;
            this.isLeaf = false;
        }

    }
    HuffmanNode root;
    public Huffman2(Map<Character, Integer> map) {
        this.root = buildTree(map);
    }

    private HuffmanNode buildTree(Map<Character, Integer> map) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>((a, b) -> (a.freq - b.freq));
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }
        while (pq.size() > 1) {
            HuffmanNode e1 = pq.poll();
            HuffmanNode e2 = pq.poll();
            HuffmanNode node = new HuffmanNode();
            node.left = e1;
            node.right = e2;
            node.freq = e1.freq + e2.freq;
            pq.add(node);
        }
        return pq.poll();
    }

    public List<String> encode() {
        List<String> result = new ArrayList<>();
        dfsFindAllNode(result, root, "");
        return result;
    }

    public String decode(String path) {
        return dfsFindNode(path, 0, root);
    }

    private String dfsFindNode(String path, int pos, HuffmanNode root) {
        if (root.isLeaf) {
            return root.val + " " + root.freq;
        }
        boolean left = path.charAt(pos) == '0';
        if (left) {
            return dfsFindNode(path, pos + 1, root.left);
        } else {
           return dfsFindNode(path, pos + 1, root.right);
        }
    }

    private void dfsFindAllNode(List<String> result, HuffmanNode root, String s) {
        if (root.isLeaf) {
            result.add(root.val + ":" + s);
            return;
        }
        dfsFindAllNode(result, root.left, s + "0");
        dfsFindAllNode(result, root.right, s + "1");
    }

    public static void main(String[] args) {
        Map<Character, Integer> map = new HashMap<>();
        map.put('C', 6);
        map.put('B', 1);
        map.put('D', 3);
        map.put('A', 5);
        Huffman2 tree = new Huffman2(map);
        System.out.println(tree.encode());
        System.out.println(tree.decode("101"));
    }
}


