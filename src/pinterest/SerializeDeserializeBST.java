package pinterest;

import Bloomberg.TreeNode;

import java.util.*;

public class SerializeDeserializeBST {
    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        preOrderTree(root, sb);
        return sb.toString().trim();
    }

    private void preOrderTree(TreeNode root, StringBuilder sb) {
        if (root == null) {
            sb.append("null" + " ");
            return;
        }
        sb.append(root.val + " ");
        preOrderTree(root.left, sb);
        preOrderTree(root.right, sb);
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        Queue<String> queue = new LinkedList<>();
        String[] arr = data.split(" ");
        queue.addAll(Arrays.asList(arr));
        return deserializeTree(queue);
    }

    private TreeNode deserializeTree(Queue<String> queue) {
        String cur = queue.poll();
        if (cur.equals("null")) {
            return null;
        }
        TreeNode root = new TreeNode(Integer.parseInt(cur));
        root.left = deserializeTree(queue);
        root.right = deserializeTree(queue);
        return root;
    }

    public static void main(String[] args) {
        TreeNode root1 = new TreeNode(1), root2 = new TreeNode(2), root3 = new TreeNode(3),
                root4 = new TreeNode(4), root5 = new TreeNode(5);
        root1.left = root2;
        root1.right = root3;
        root3.left = root4;
        root3.right = root5;
        SerializeDeserializeBST sovler = new SerializeDeserializeBST();
        System.out.println(sovler.deserialize(sovler.serialize(root1)).val);
    }
}
