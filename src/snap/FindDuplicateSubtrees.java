package snap;

import Bloomberg.TreeNode;

import java.util.*;

public class FindDuplicateSubtrees {
    public List<TreeNode> findDuplicateSubtrees(TreeNode root) {
        List<TreeNode> result = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        dfsFindAll(root, map, result);
        return result;
    }

    private String dfsFindAll(TreeNode root, Map<String, Integer> map, List<TreeNode> result) {
        if (root == null) return "#";
        String str = root.val + dfsFindAll(root.left, map, result) + dfsFindAll(root.right, map, result);
        map.put(str, map.getOrDefault(str, 0) + 1);
        if (map.get(str) == 2) {
            result.add(root);
        }
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
