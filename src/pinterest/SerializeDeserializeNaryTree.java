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
    int n;
    public SerializeDeserializeNaryTree(int n) {
        this.n = n;
    }

    // Encodes a tree to a single string.
    public String serialize(Node root) {
        if (root == null) {
            return "";
        }
        List<Node> list = new ArrayList<>();
        list.add(root);
        for (int i = 0; i < list.size(); i++) {
            Node cur = list.get(i);
            if (cur == null) {
                continue;
            }
            list.addAll(cur.children);
        }
        while (list.get(list.size() - 1) == null) {
            list.remove(list.size() - 1);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(list.get(0));
        for (int i = 1; i < list.size(); i++){
            if (list.get(i) == null) {
                sb.append(",#");
            } else {
                sb.append("," + list.get(i).val);
            }
        }
        sb.append("}");
        return sb.toString();
    }

    // Decodes your encoded data to tree.
    public Node deserialize(String data) {
        if (data.equals("{}")) {
            return null;
        }
        String[] vals = data.substring(1, data.length() - 1).split(",");
        int index = 0;
        List<Node> list = new ArrayList<>();

        Node root = new Node(Integer.parseInt(vals[0]), new ArrayList<>());
        list.add(root);

        for (int i = 1; i < vals.length; i++){
            if (vals[i].equals("#")) {
                list.get(index).children.add(null);
            } else {
                int val = Integer.parseInt(vals[i]);
                Node n = new Node(val, new ArrayList<>());
                list.get(index).children.add(n);
            }
            if (list.get(index).children.size() == n) {
                index++;
            }
        }
        return root;
    }
}
