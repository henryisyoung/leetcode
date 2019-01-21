package leetcode;
import java.util.*;
public class BSTIterator {
	Stack<TreeNode> stack = new Stack<TreeNode>();
	TreeNode cur;
    public BSTIterator(TreeNode root) {
        cur = root;
    }

    /** @return whether we have a next smallest number */
    public boolean hasNext() {
        return (cur != null) || (!stack.isEmpty());
    }

    /** @return the next smallest number */
    public int next() {
    	int result = 0;
        while(cur != null){
        	stack.push(cur);
        	cur = cur.left; 
        }
        	TreeNode n = stack.pop();
        	result = n.val;
        	cur = n.right;
        
        return result;
    }
}

/**
 * Your BSTIterator will be called like this:
 * BSTIterator i = new BSTIterator(root);
 * while (i.hasNext()) v[f()] = i.next();
 */