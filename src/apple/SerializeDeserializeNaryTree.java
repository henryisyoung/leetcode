package apple;

import java.util.*;

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
public class SerializeDeserializeNaryTree {
    public String serialize(Node root) {
        if (root == null) return "";
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }

    private void serializeHelper(Node root, StringBuilder sb) {
        if (root == null) return ;
        int val = root.val, size = root.children.size();
        sb.append(val + "," + size + ",");
        for (Node child : root.children) {
            serializeHelper(child, sb);
        }
    }

    // Decodes your encoded data to tree.
    public Node deserialize(String data) {
        if (data == null || data.length() == 0) return null;
        String[] nodes = data.split(",");
        Queue<String> queue = new LinkedList<String>();
        queue.addAll(Arrays.asList(nodes));
        return deserializeHelper(queue);
    }

    private Node deserializeHelper(Queue<String> queue) {
        if (queue.isEmpty()) return null;
        int val = Integer.parseInt(queue.poll()), size = Integer.parseInt(queue.poll());
        Node node = new Node(val, new ArrayList<>());
        for (int i = 0; i < size; i++) {
            Node next = deserializeHelper(queue);
            if (next != null) node.children.add(next);
        }
        return node;
    }
}
