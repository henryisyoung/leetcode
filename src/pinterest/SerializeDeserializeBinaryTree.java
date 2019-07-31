package pinterest;

import Bloomberg.TreeNode;

import java.util.*;

public class SerializeDeserializeBinaryTree {
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        if (root == null) return "{}";
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

        sb.append("{");
        sb.append(list.get(0).val);
        for (int i = 1; i < list.size(); i++) {
            TreeNode n = list.get(i);
            if (n == null) {
                sb.append(",#");
            } else {
                sb.append("," + n.val);
            }
        }
        sb.append("}");
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
        if (data.equals("{}")) return null;
        String[] nodes = data.substring(1, data.length() - 1).split(",");
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

    public static void main(String[] args) {
        TreeNode root1 = new TreeNode(1), root2 = new TreeNode(2), root3 = new TreeNode(3),
                root4 = new TreeNode(4), root5 = new TreeNode(5);
        root1.left = root2;
        root1.right = root3;
        root3.left = root4;
        root3.right = root5;
        SerializeDeserializeBinaryTree sovler = new SerializeDeserializeBinaryTree();
        System.out.println(sovler.deserialize(sovler.serialize(root1)).val);
    }
}
