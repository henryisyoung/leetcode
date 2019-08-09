package snap;

import Bloomberg.TreeNode;

import java.util.*;

public class FindDuplicateSubtrees {
    public List<TreeNode> findDuplicateSubtrees(TreeNode root) {
        List<TreeNode> result = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        traverseTree(root, map, result);
        return result;
    }

    private String traverseTree(TreeNode root, Map<String, Integer> map, List<TreeNode> result) {
        if (root == null) return "#";
        String str = root.val + "," + traverseTree(root.left, map, result) + "," + traverseTree(root.right, map, result);
        System.out.println(str);
        if (map.containsKey(str) && map.get(str) == 1) result.add(root);
        map.put(str, map.getOrDefault(str , 0) + 1);
        return str;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        TreeNode r2 = root.left, r3 = root.right;
        r2.left = new TreeNode(4);
        r3.right = new TreeNode(4);
        TreeNode r4 = r3.left = new TreeNode(2);
        r4.left = new TreeNode(4);
        FindDuplicateSubtrees solver = new FindDuplicateSubtrees();
        System.out.println(solver.findDuplicateSubtrees(root));
    }
}
