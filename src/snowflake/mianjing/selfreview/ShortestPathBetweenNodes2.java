package snowflake.mianjing.selfreview;

import Bloomberg.TreeNode;

/*
Finding the Shortest Path Between Two Nodes in a Binary Tree
Problem Overview
You are provided with the root of a binary tree that contains n nodes. Every node in this tree has a unique value between 1 and n. You are also given two specific numbers: startValue and destValue.

Your task is to find the shortest path to travel from the node labeled startValue to the node labeled destValue. You must return this path as a string.

Use the following letters to represent the movements:

'L': Move down to the left child node.
'R': Move down to the right child node.
'U': Move up to the parent node.
Sample Cases
Case 1:

Input: root = [5,1,2,3,null,6,4], startValue = 3, destValue = 6

Output: "UURL"

Case 2:

Input: root = [2,1], startValue = 2, destValue = 1

Output: "L"

Input Limits
The number of nodes (n) is between 2 and 100,000 (10^5).
Each Node.val is between 1 and n.
All values in the tree are unique (no duplicates).
The startValue is never the same as the destValue.
Both startValue and destValue are guaranteed to be inside the tree.
 */

/**
 * Shortest path between two values in a binary tree, written as a string of
 * 'L' (left child), 'R' (right child), 'U' (parent).
 *
 * Idea
 * ----
 * The shortest path always goes start → LCA → dest, because the tree has no
 * cross edges. Instead of explicitly finding the LCA we use a slick trick:
 *
 *   1. Find the root→start path        (in 'L'/'R' alphabet).
 *   2. Find the root→dest  path        (in 'L'/'R' alphabet).
 *   3. Strip the common prefix         (that's the root→LCA portion).
 *   4. Every remaining char of path→start becomes a 'U' (climb up to LCA).
 *   5. Append the remaining suffix of path→dest unchanged (descend to dest).
 *
 * Why it's correct: after step 3 the two paths diverge at the LCA. Climbing
 * up the start side undoes those L/R steps; the dest side is exactly the
 * descent from LCA to dest.
 *
 * Why it's the SHORTEST: any path between two tree nodes must pass through
 * their LCA (no cycles), and the LCA-routed path uses one move per edge with
 * no detours.
 *
 * Time:  O(n) — two DFS traversals + linear prefix scan.
 * Space: O(h) recursion (or explicit stack) + O(n) path strings.
 */
public class ShortestPathBetweenNodes2 {

    public String getDirections(TreeNode root, int startValue, int destValue) {
        StringBuilder startPath = new StringBuilder(), destPath = new StringBuilder();
        dfsFindPath(root, startPath, startValue);
        dfsFindPath(root, destPath, destValue);
        int min = Math.min(startPath.length(), destPath.length());
        int index = 0;
        while (index < min && startPath.charAt(index) == destPath.charAt(index)) index++;
        StringBuilder result = new StringBuilder();
        int k = startPath.length() - index;
        for (int i = 0; i < k; i++) {
            result.append("U");
        }

        result.append(destPath.substring(index, destPath.length()));
        return result.toString();
    }

    private boolean dfsFindPath(TreeNode root, StringBuilder path, int val) {
        if (root == null) return false;
        if (root.val == val) return true;
        path.append("L");
        if (dfsFindPath(root.left, path, val)) {
            return true;
        }
        path.deleteCharAt(path.length() - 1);

        path.append("R");
        if (dfsFindPath(root.right, path, val)) {
            return true;
        }
        path.deleteCharAt(path.length() - 1);
        return false;
    }
}
