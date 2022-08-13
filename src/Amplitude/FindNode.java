package Amplitude;

import java.util.ArrayList;
import java.util.List;

public class FindNode {
    class Node{
        Node parent;
        List<Node> children;
        int val;
        public Node(int val) {
            this.val = val;
        }
    }

    public Node findNode(Node root1, Node root2, Node a) {
        List<Integer> list = new ArrayList<>();

        Node parent = a.parent;
        while (parent != root1) {
            int index = findIndex(a, parent);
            if (index == -1) {
                return null;
            }
            list.add(0, index);
            a = parent;
            parent = a.parent;
        }

        Node b = null;
        for (int index : list) {
            b = root2.children.get(index);
            root2 = b;
        }

        return b;
    }

    private int findIndex(Node a, Node parent) {

        for (int i = 0; i < parent.children.size(); i++) {
            if (a == parent.children.get(i)) {
                return i;
            }
        }
        return -1;
    }
}
