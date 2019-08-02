package pinterest;

import java.util.*;

public class SerializeDeserializeNaryTree {
    class Node {
        public int val;
        public List<Node> children;

        public Node() {}

        public Node(int _val,List<Node> _children) {
            val = _val;
            children = _children;
        }
    };


    // Encodes a tree to a single string.
    public String serialize(Node root) {
        StringBuilder sb = new StringBuilder();
        trasverseTree(root, sb);
        return sb.toString();
    }

    private void trasverseTree(Node root, StringBuilder sb) {
        if (root == null) return;
        sb.append(root.val).append(",");
        sb.append("#").append(Integer.toString(root.children.size())).append(",");
        for (Node n : root.children) {
            trasverseTree(n, sb);
        }
    }

    // Decodes your encoded data to tree.
    public Node deserialize(String data) {
        if(data == null || data.length() == 0) return null;
        String[] array = data.split(",");
        Queue<String> queue = new LinkedList<>(Arrays.asList(array));
        return deserializeTree(queue);
    }

    private Node deserializeTree(Queue<String> queue) {
        if(queue.isEmpty()) return null;
        String rootVal = queue.poll();
        String size = queue.poll();
        int num = Integer.valueOf(size.substring(1));
        Node root = new Node(Integer.valueOf(rootVal), new ArrayList<>());
        for (int i = 0; i < num; i++) {
            Node child = deserializeTree(queue);
            if (child == null) continue;
            root.children.add(child);
        }
        return root;
    }
}
