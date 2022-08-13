package google.vo.mianjing;

import Bloomberg.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MostFrequentSubtreeSum {
    // 508
    public int[] findFrequentTreeSum(TreeNode root) {
        Map<Integer, Integer> map = new HashMap<>();
        if (root == null) {
            return new int[0];
        }
        findAllNodes(root, map);

        int max = 0;
        for (int key : map.keySet()) {
            int count = map.get(key);
            if (count > max) {
                max = count;
            }
        }
        if (max == 1) {
            int[] result = new int[map.size()];
            int i = 0;
            for (int key : map.keySet()) {
                result[i++] = key;
            }
            return result;
        }
        List<Integer> list = new ArrayList<>();
        for (int key : map.keySet()) {
            if (map.get(key) == max) {
                list.add(key);
            }
        }
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    private int findAllNodes(TreeNode root, Map<Integer, Integer> map) {
        if (root == null) {
            return 0;
        }
        int left = findAllNodes(root.left, map), right = findAllNodes(root.right, map);
        int sum = left + right + root.val;
        map.put(sum, map.getOrDefault(sum, 0) + 1);
        return sum;
    }
}
