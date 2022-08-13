package uber.phone.leetcode;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SerializeDeserializeNaryTree {
    class Node {
        public int val;
        public List<Node> children;

        public Node() {
        }

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, List<Node> _children) {
            val = _val;
            children = _children;
        }
    }

    ;

    public String serialize(Node root) {
        if (root == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(root.val + "," + root.children.size() + ",");
        for (Node node : root.children) {
            sb.append(serialize(node));
        }
        return sb.toString();

    }

    public Node deserialize(String data) {
        if (data.equals("")) {
            return null;
        }
        String[] nodes = data.split(",");
        Queue<String> queue = new LinkedList<>();
        queue.addAll(Arrays.asList(nodes));
        return deserializeHelper(queue);
    }

    private Node deserializeHelper(Queue<String> queue) {
        if (queue.isEmpty()) {
            return null;
        }
        Node root = new Node(Integer.parseInt(queue.poll()));
        int size = Integer.parseInt(queue.poll());
        for (int i = 0; i < size; i++) {
            Node child = deserializeHelper(queue);
            if (child != null) {
                root.children.add(child);
            }
        }
        return root;
    }
}