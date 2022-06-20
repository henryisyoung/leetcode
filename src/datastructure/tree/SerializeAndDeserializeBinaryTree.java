package datastructure.tree;

import java.util.ArrayList;
import java.util.List;

public class SerializeAndDeserializeBinaryTree {
    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        if (root == null) return "";

        List<TreeNode> list = new ArrayList<>();
        list.add(root);

        for (int i = 0; i < list.size(); i++) {
            TreeNode cur = list.get(i);
            if (cur == null) continue;
            list.add(cur.left);
            list.add(cur.right);
        }
        int i = list.size() - 1;
        while (i >= 0 && list.get(i) == null) {
            list.remove(i--);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(list.get(0).val);

        for (i = 1; i < list.size(); i++) {
            TreeNode cur = list.get(i);
            if (cur == null) {
                sb.append(",#");
            } else {
                sb.append("," + cur.val);
            }
        }

        return sb.toString();
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        if (data.equals("")) return null;
        String[] strs = data.split(",");

        TreeNode root = new TreeNode(Integer.parseInt(strs[0]));
        List<TreeNode> list = new ArrayList<>();
        list.add(root);
        int index = 0;
        boolean isLeft = true;

        for (int i = 1; i < strs.length; i++) {
            String str = strs[i];
            if (!str.equals("#")) {
                TreeNode cur = new TreeNode(Integer.parseInt(str));
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
