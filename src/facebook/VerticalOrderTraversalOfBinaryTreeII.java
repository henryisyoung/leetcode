package facebook;

import Bloomberg.TreeNode;

import java.util.*;

public class VerticalOrderTraversalOfBinaryTreeII {
    class Node{
        int x, y, val;
        public Node(int x, int y, int val) {
            this.x = x;
            this.y = y;
            this.val = val;
        }
    }

    public List<List<Integer>> verticalTraversal(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        List<Node> nodes = new ArrayList<>();
        dfsFindAll(root, 0, 0, nodes);
        Collections.sort(nodes, new Comparator<Node>(){
            @Override
            public int compare(Node a, Node b) {
                if(a.x != b.x) return a.x - b.x;
                if(a.y != b.y) return a.y - b.y;
                return a.val - b.val;
            }
        });
        int prevX = nodes.get(0).x;
        result.add(new ArrayList<>());
        result.get(0).add(nodes.get(0).val);
        for(int i = 1; i < nodes.size(); i++) {
            int curX = nodes.get(i).x;
            if(curX != prevX) {
                result.add(new ArrayList<>());
            }
            prevX = curX;
            result.get(result.size() - 1).add(nodes.get(i).val);
        }

        return result;
    }

    private void dfsFindAll(TreeNode root, int x, int y, List<Node> nodes) {
        if(root == null) return;
        nodes.add(new Node(x, y, root.val));
        dfsFindAll(root.left, x - 1, y + 1, nodes);
        dfsFindAll(root.right, x + 1, y + 1, nodes);
    }
}
