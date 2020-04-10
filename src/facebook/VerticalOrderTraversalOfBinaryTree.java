package facebook;

import Bloomberg.TreeNode;

import java.util.ArrayList;
import java.util.*;

public class VerticalOrderTraversalOfBinaryTree {
    public List<List<Integer>> verticalTraversal(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        Map<Integer, List<Integer>> map = new TreeMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.pos == o2.pos) return o1.n.val - o2.n.val;
                return o1.pos - o2.pos;
            }
        });
        queue.add(new Node(root, 0));

        map.put(0, new ArrayList<>());
        map.get(0).add(root.val);

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Node cur = queue.poll();
                TreeNode node = cur.n;
                int pos = cur.pos;
                if (node.left != null) {
                    map.putIfAbsent(pos - 1, new ArrayList<>());
                    map.get(pos - 1).add(node.left.val);
                    queue.add(new Node(node.left, pos - 1));
                }
                if (node.left != null) {
                    map.putIfAbsent(pos + 1, new ArrayList<>());
                    map.get(pos + 1).add(node.right.val);
                    queue.add(new Node(node.right, pos + 1));
                }
            }
        }
        for (List<Integer> values : map.values()) {
            result.add(values);
        }
        return result;
    }

    class Node{
        TreeNode n;
        int pos;
        public Node(TreeNode n, int pos) {
            this.pos = pos;
            this.n = n;
        }
    }
}
