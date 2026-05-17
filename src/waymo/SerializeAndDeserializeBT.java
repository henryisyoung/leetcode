package waymo;

import Bloomberg.TreeNode;

import java.util.*;

public class SerializeAndDeserializeBT {
    public String serialize(TreeNode root) {
        if (root == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        findAllSerialize(root, sb);
        return sb.toString().trim();
    }

    private void findAllSerialize(TreeNode root, StringBuilder sb) {
        if (root == null) {
            sb.append("null ");
            return;
        }
        sb.append(root.val + " ");
        findAllSerialize(root.left, sb);
        findAllSerialize(root.right, sb);
    }

    public TreeNode deserialize(String data) {
        if (data.equals("")) {
            return null;
        }

        String[] vals = data.split(" ");
        Queue<String> queue = new LinkedList<>();
        queue.addAll(Arrays.asList(vals));

        return findAllDeserialize(queue);
    }

    private TreeNode findAllDeserialize(Queue<String> queue) {
        if (queue.isEmpty()) {
            return null;
        }
        String node = queue.poll();
        if (node.equals("null")) {
            return null;
        }
        int val = Integer.parseInt(node);
        TreeNode cur = new TreeNode(val);
        TreeNode left = findAllDeserialize(queue);
        TreeNode right = findAllDeserialize(queue);

        cur.left = left;
        cur.right = right;

        return cur;
    }
}
