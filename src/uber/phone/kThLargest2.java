package uber.phone;

import Bloomberg.TreeNode;

import java.util.Stack;

// https://www.geeksforgeeks.org/kth-smallest-element-in-bst-using-o1-extra-space/
public class kThLargest2 {

    public static int findKthLargestBST(TreeNode root, int k) {
        Stack<TreeNode> stack = new Stack<>();
        while (root != null) {
            stack.push(root);
            root = root.right;
        }

        while (!stack.isEmpty()) {
            TreeNode cur = stack.pop();
            k--;
            if (k == 0) {
                return cur.val;
            }
            TreeNode node = cur.left;
            while (node != null) {
                stack.push(node);
                node = node.right;
            }
        }
        throw new IllegalStateException("not valid k");
    }

    public static int morrisTraversalKthLargest(TreeNode root, int k) {
        int result = -1;
        TreeNode cur = root;
        while (cur != null) {
            if (cur.right == null) {
                k--;
                if (k == 0) {
                    result = cur.val;
                }
                cur = cur.left;
            } else {
                TreeNode pre = cur.right;
                while (pre.left != null && pre.left != cur) {
                    pre = pre.left;
                }
                if (pre.left == null) {
                    pre.left = cur;
                    cur = cur.right;
                } else if (pre.left == cur) {
                    pre.left = null;
                    k--;
                    if (k == 0) {
                        result = cur.val;
                    }
                    cur = cur.left;
                }
            }
        }
        if (result == -1) {
            throw new IllegalStateException("not valid k");
        }
        return result;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(5);

        TreeNode a = new TreeNode(2);
        TreeNode b = new TreeNode(7);
        TreeNode c = new TreeNode(1);
        TreeNode d = new TreeNode(3);
        TreeNode e = new TreeNode(6);
        TreeNode f = new TreeNode(8);
        root.left = a;
        root.right = b;
        a.left = c;
        a.right = d;
        b.left = e;
        b.right = f;
//        for (int i = 1; i <= 7; i++) {
//            System.out.println(findKthLargestBST(root, i));;
//        }
//        System.out.println();
//        for (int i = 1; i <= 7; i++) {
//            System.out.println(morrisTraversal(root, i));;
//        }
//        System.out.println();
        for (int i = 1; i <= 7; i++) {
            System.out.println(morrisTraversalKthLargest(root, i));;
        }
    }
}
