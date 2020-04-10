package facebook;

import Bloomberg.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class _SerializeDeserializeBinaryTree {
    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        if (root == null) return "";
        List<TreeNode> list = new ArrayList<>();
        list.add(root);
        for (int i = 0; i < list.size(); i++) {
            TreeNode node = list.get(i);
            if (node == null) continue;
            list.add(node.left);
            list.add(node.right);
        }

        while (list.size() > 0 && list.get(list.size() - 1) == null) {
            list.remove(list.size() - 1);
        }

        sb.append(list.get(0).val);
        for (int i = 1; i < list.size(); i++) {
            TreeNode n = list.get(i);
            if (n == null) {
                sb.append(",#");
            } else {
                sb.append("," + n.val);
            }
        }
        return sb.toString();
    }

    /**
     * This method will be invoked second, the argument data is what exactly
     * you serialized at method "serialize", that means the data is not given by
     * system, it's given by your own serialize method. So the format of data is
     * designed by yourself, and deserialize it here as you serialize it in
     * "serialize" method.
     */
    public TreeNode deserialize(String data) {
        if (data.equals("")) return null;
        String[] nodes = data.split(",");
        boolean isLeft = true;
        int index = 0;
        List<TreeNode> list = new ArrayList<>();
        TreeNode root =  new TreeNode(Integer.parseInt(nodes[0]));
        list.add(root);
        for (int i = 1; i < nodes.length; i++) {
            if (!nodes[i].equals("#")) {
                TreeNode c = new TreeNode(Integer.parseInt(nodes[i]));
                if (isLeft) {
                    list.get(index).left = c;
                } else {
                    list.get(index).right = c;
                }
                list.add(c);
            }
            if (!isLeft) index++;
            isLeft = !isLeft;
        }
        return root;
    }
}
