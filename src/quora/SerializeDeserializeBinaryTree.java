package quora;

import leetcode.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class SerializeDeserializeBinaryTree {
    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        if (root == null) {
            return "{}";
        }
        List<TreeNode> list = new ArrayList<>();
        list.add(root);
        for (int i = 0; i < list.size(); i++) {
            TreeNode node = list.get(i);
            if (node == null) {
                continue;
            }
            list.add(node.left);
            list.add(node.right);
        }
        while (list.get(list.size() - 1) == null) {
            list.remove(list.size() - 1);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(list.get(0).val);
        for (int i = 1; i < list.size(); i++) {
            TreeNode node = list.get(i);
            if (node == null) {
                sb.append(",#");
            } else {
                sb.append("," + node.val);
            }
        }
        sb.append("}");
        return sb.toString();
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        if (data.equals("{}")) {
            return null;
        }
        String[] nodes = data.substring(1, data.length() - 1).split(",");
        TreeNode root = new TreeNode(Integer.parseInt(nodes[0]));
        int index = 0;
        boolean isLeft = true;
        List<TreeNode> list = new ArrayList<>();
        list.add(root);
        for (int i = 1; i < nodes.length; i++) {
            if (!nodes[i].equals("#")) {
                TreeNode cur = new TreeNode(Integer.parseInt(nodes[i]));
                if (isLeft) {
                    list.get(index).left = cur;
                } else {
                    list.get(index).right = cur;
                }
                list.add(cur);
            }
            if (!isLeft) {
                index++;
            }
            isLeft = !isLeft;
        }

        return root;
    }
}
