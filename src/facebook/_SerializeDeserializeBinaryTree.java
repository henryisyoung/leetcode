package facebook;

import Bloomberg.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class _SerializeDeserializeBinaryTree {
    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        if (root == null) return "";
        StringBuilder sb = new StringBuilder();

        List<TreeNode> nodes = new ArrayList<>();
        nodes.add(root);
        for (int i = 0; i < nodes.size(); i++) {
            TreeNode cur = nodes.get(i);
            if (cur == null) continue;
            nodes.add(cur.left);
            nodes.add(cur.right);
        }

        while (nodes.size() > 0 && nodes.get(nodes.size() - 1) == null) nodes.remove(nodes.size() - 1);

        sb.append(nodes.get(0).val);

        for (int i = 1; i < nodes.size(); i++) {
            TreeNode cur = nodes.get(i);
            if (cur == null) {
                sb.append(",#");
            } else {
                sb.append("," + cur.val);
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
        if (data.length() == 0) return null;
        String[] nodes = data.split(",");
        TreeNode root = new TreeNode(Integer.parseInt(nodes[0]));

        int index = 0;
        List<TreeNode> list = new ArrayList<>();
        list.add(root);

        boolean isLeft = true;

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
            if (!isLeft) index++;

            isLeft = !isLeft;
        }

        return root;
    }
}
