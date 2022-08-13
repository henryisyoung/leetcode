package google.vo;

import Bloomberg.TreeNode;

import java.util.*;

public class FindDuplicateSubtrees {
    public List<TreeNode> findDuplicateSubtrees(TreeNode root) {
        List<TreeNode> result = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        findAll(map, result, root);
        return result;
    }

    private String findAll(Map<String, Integer> map, List<TreeNode> result, TreeNode root) {
        if (root == null) {
            return "#";
        }
        String left = findAll(map, result, root.left);
        String right = findAll(map, result, root.right);
        String str = root.val + "," + left + right;
        if (map.getOrDefault(str, 0) == 1) {
            result.add(root);
        }
        map.put(str, map.getOrDefault(str, 0) + 1);
        return str;
    }

    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();

        System.out.println(map.getOrDefault("asd", 0) == 1);
    }
}
