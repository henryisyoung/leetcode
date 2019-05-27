package google;

import java.util.*;

public class DeleteFatherNode {
    //https://www.1point3acres.com/bbs/thread-519035-1-1.html
    static class TreeNode{
        int fatherIndex;
        char val;
        public TreeNode(int fatherIndex, char val) {
            this.fatherIndex = fatherIndex;
            this.val = val;
        }

        @Override
        public String toString() {
            return "{" + fatherIndex + "," + val + "}";
        }
    }
    public static List<TreeNode> deleteSingleNode(List<TreeNode> tree, TreeNode target) {
        List<TreeNode> result = new ArrayList<>();
        List<TreeNode> childrenOfDeleted = new ArrayList<>();
        Map<Character, Integer> nodeToIndex = new HashMap<>();
        for (TreeNode n : tree) {
            if (n.fatherIndex >= 0 && tree.get(n.fatherIndex).val == target.val) {
                childrenOfDeleted.add(n);
            }
        }
        boolean beforeDelete = true;
        for (TreeNode n : tree) {
            if (beforeDelete) {
                if (n.val != target.val) {
                    result.add(n);
                    nodeToIndex.put(n.val, result.size() - 1);
                } else {
                    for (TreeNode child : childrenOfDeleted) {
                        result.add(new TreeNode(target.fatherIndex, child.val));
                        nodeToIndex.put(child.val, result.size() - 1);
                    }
                    beforeDelete = false;
                }
            } else {
                int pos = n.fatherIndex;

                if (pos >= 0) {
                    char c = tree.get(pos).val;
                    if (nodeToIndex.containsKey(c)) {
                        result.add(new TreeNode(nodeToIndex.get(tree.get(pos).val), n.val));
                    }
                }
                nodeToIndex.put(n.val, result.size() - 1);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        // input tree = {{-1, 'A'}, {0, 'B'}, {0, 'C'}, {1, 'D'}, {1, 'E'}, {2, 'F'}, {3,'G}}
        // output  {{-1,'A'}, {0, 'D'}, {0, 'E'}, {0, 'C'}, {1, 'G'}, {3, 'F'}}
        TreeNode a = new TreeNode(-1, 'A'),
        b = new TreeNode(0, 'B'),
        c = new TreeNode(0, 'C'),
        d = new TreeNode(1, 'D'),
        e = new TreeNode(1, 'E'),
        f = new TreeNode(2, 'F'),
        g = new TreeNode(3, 'G');
        List<TreeNode> tree = Arrays.asList(a, b, c, d, e, f, g);
        TreeNode target = new TreeNode(0, 'B');
        List<TreeNode> result = deleteSingleNode(tree, target);
        System.out.println(result.toString());
    }
}
