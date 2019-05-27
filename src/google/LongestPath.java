package google;

import java.util.List;
import java.util.Map;

public class LongestPath {
    // https://www.1point3acres.com/bbs/thread-518250-1-1.html
    class TreeNode {
        int time;
        List<TreeNode> children;
        public TreeNode(int time, List<TreeNode> children) {
            this.time = time;
            this.children = children;
        }
    }
    int max = 0;
    public int maxTime(TreeNode root) {
        if (root == null) {
            return 0;
        }
        for (TreeNode child : root.children) {
            int time = maxTime(child);
            max = Math.max(max, time + child.time);
        }
        return max;
    }
}
