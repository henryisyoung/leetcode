package apple;

import java.util.*;

public class SerializeandDeserializeNaryTree {
    class Node {
        public int val;
        public List<Node> children;

        public Node() {}

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, List<Node> _children) {
            val = _val;
            children = _children;
        }
    };
    
    // Encodes a tree to a single string.
    public String serialize(Node root) {
        if (root == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        dfsSerialize(root, sb);
        return sb.toString();
    }

    private void dfsSerialize(Node root, StringBuilder sb) {
        if (root == null) return;
        sb.append(root.val + "," + root.children.size() + ",");
        for (Node child : root.children) {
            dfsSerialize(child, sb);
        }
    }

    // Decodes your encoded data to tree.
    public Node deserialize(String data) {
        if (data.equals("")) {
            return null;
        }
        String[] nodes = data.split(",");
        Queue<String> queue = new LinkedList<>();
        queue.addAll(Arrays.asList(nodes));
        return dfsDeserializeHelper(queue);
    }

    private Node dfsDeserializeHelper(Queue<String> queue) {
        if (queue.isEmpty()) return null;
        int val = Integer.parseInt(queue.poll());
        int size = Integer.parseInt(queue.poll());
        Node node = new Node(val, new ArrayList<>());
        for (int i = 0; i < size; i++) {
            Node child = dfsDeserializeHelper(queue);
            if (child != null) node.children.add(child);
        }
        return node;
    }
}
