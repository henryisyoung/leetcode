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
    }
    // Encodes a tree to a single string.
    public String serialize(Node root) {
        StringBuilder sb = new StringBuilder();
        serializehelper(root, sb);
        return sb.toString();
    }

    private void serializehelper(Node root, StringBuilder sb) {
        if (root == null) return;
        sb.append(root.val + ",");
        sb.append(root.children.size() + ",");
        for (Node next : root.children) {
            serializehelper(next, sb);
        }
    }

    // Decodes your encoded data to tree.
    public Node deserialize(String data) {
        if(data == null || data.length() == 0) return null;

        String[] vals = data.split(",");
        Queue<String> queue = new LinkedList<>();
        queue.addAll(Arrays.asList(vals));
        return deserializeHelper(queue);
    }

    private Node deserializeHelper(Queue<String> queue) {
        if (queue.isEmpty()) return null;
        String val = queue.poll();
        int size =Integer.parseInt(queue.poll());
        Node root = new Node(Integer.parseInt(val), new ArrayList<>());
        for (int i = 0; i < size; i++) {
            Node next = deserializeHelper(queue);
            if (next != null) root.children.add(next);
        }
        return root;
    }

}
