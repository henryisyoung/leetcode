package uber;

import Bloomberg.TreeNode;

import java.util.*;

public class AllNodesDistanceKInBinaryTree {
    public List<Integer> distanceK(TreeNode root, TreeNode target, int K) {
        Map<TreeNode, List<TreeNode>> map = new HashMap();
        dfs(root, null, map);

        Queue<TreeNode> queue = new LinkedList();
        queue.add(target);

        Set<TreeNode> seen = new HashSet();
        seen.add(target);
        List<Integer> result  = new ArrayList<>();
        int dist = 0;
        while (!queue.isEmpty() & dist <= K) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode cur = queue.poll();
                if (dist == K) {
                    if(cur != null) result.add(cur.val);
                }
                for (TreeNode next : map.get(cur)) {
                    if (seen.contains(next)) continue;
                    queue.add(next);
                    seen.add(next);
                }
            }
            dist++;
        }

        return result;
    }

    public void dfs(TreeNode node, TreeNode par, Map<TreeNode, List<TreeNode>> map) {
        if (node != null) {
            map.putIfAbsent(node, new ArrayList<>());
            map.get(node).add(par);
            map.putIfAbsent(par, new ArrayList<>());
            map.get(par).add(node);
            dfs(node.left, node, map);
            dfs(node.right, node, map);
        }
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(5);
        root.right = new TreeNode(1);
        AllNodesDistanceKInBinaryTree solution = new AllNodesDistanceKInBinaryTree();
        List<Integer> result = solution.distanceK(root, root.left, 2);
    }
}
