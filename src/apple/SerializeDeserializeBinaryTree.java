package apple;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SerializeDeserializeBinaryTree {

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }

    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        if (root == null) return "";
        StringBuilder sb = new StringBuilder();
        List<TreeNode> list = new ArrayList<>();
        list.add(root);
        for (int i = 0; i < list.size(); i++) {
            TreeNode node = list.get(i);
            if (node != null) {
                list.add(node.left);
                list.add(node.right);
            }
        }
        while (list.size() > 0 && list.get(list.size() - 1) == null) {
            list.remove(list.size() - 1);
        }
        sb.append(root.val);
        for (int i = 1; i < list.size(); i++) {
            TreeNode node = list.get(i);
            if (node == null) {
                sb.append(",#");
            } else {
                sb.append("," + node.val);
            }
        }
        return sb.toString();
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        if (data == null || data.length() == 0) return null;
        String[] nodes = data.split(",");
        boolean isLeft = true;
        TreeNode root = new TreeNode(Integer.parseInt(nodes[0]));
        List<TreeNode> list = new ArrayList<>();
        list.add(root);

        int index = 0;

        for (int i = 1; i < nodes.length; i++) {
            String cur = nodes[i];
            if (!cur.equals("#")) {
                TreeNode n = new TreeNode(Integer.parseInt(cur));
                if (isLeft) {
                    list.get(index).left = n;

                } else {
                    list.get(index).right = n;
                }
                list.add(n);
            }
            if (!isLeft) index++;
            isLeft = !isLeft;
        }

        return root;
    }
}
