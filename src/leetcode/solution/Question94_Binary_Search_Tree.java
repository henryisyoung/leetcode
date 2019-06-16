package leetcode.solution;
import java.util.*;
public class Question94_Binary_Search_Tree {
    // non-recursive inorder traversal
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if(root == null) return result;
        Stack<TreeNode> stack = new Stack<>();
        while(root != null){
            stack.push(root);
            root = root.left;
        }
        while (!stack.isEmpty()){
            TreeNode cur = stack.pop();
            result.add(cur.val);
            if(cur.right != null){
                TreeNode n = cur.right;
                while (n != null){
                    stack.push(n);
                    n = n.left;
                }
            }
        }
        return result;
    }
}
