package uber.phone;

import Bloomberg.TreeNode;

// https://www.geeksforgeeks.org/kth-smallest-element-in-bst-using-o1-extra-space/
public class kThLargest {
    static int count, val;
    public static int findKthLargestBST(TreeNode root, int k) {
        count = val = 0;
        dfsKth(root, k);
        return val;
    }

    private static void dfsKth(TreeNode root, int k) {
        if (root == null) {
            return;
        }
        dfsKth(root.right, k);
        if (++count == k) {
            val = root.val;
        }
        dfsKth(root.left, k);
        return;
    }

    public static int morrisTraversal(TreeNode root, int k) {
        int count = 0;
        int ksmall = Integer.MIN_VALUE; // store the Kth smallest
        TreeNode curr = root; // to store the current node

        while (curr != null) {
            if (curr.left == null) {
                count++;

                if (count==k)
                    ksmall = curr.val;

                curr = curr.right;
            }
            else {

                TreeNode pre = curr.left;
                while (pre.right != null && pre.right != curr)
                    pre = pre.right;

                if (pre.right== null) {
                    pre.right = curr;
                    curr = curr.left;
                }
                else {
                    pre.right = null;
                    count++;
                    if (count==k)
                        ksmall = curr.val;
                    curr = curr.right;
                }
            }
        }
        return ksmall;
    }

    public static int morrisTraversalKthLargest(TreeNode root, int k) {
        TreeNode cur = root;
        int kth = 0, count = 0;

        while (cur != null) {
            if (cur.right == null) {
                count++;
                if (count == k) {
                    kth = cur.val;
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
                } else {
                    pre.left = null;
                    count++;
                    if (count == k) {
                        kth = cur.val;
                    }
                    cur = cur.left;
                }
            }
        }
        return kth;
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
        for (int i = 1; i <= 7; i++) {
            System.out.println(findKthLargestBST(root, i));;
        }
        System.out.println();
        for (int i = 1; i <= 7; i++) {
            System.out.println(morrisTraversal(root, i));;
        }
        System.out.println();
        for (int i = 1; i <= 7; i++) {
            System.out.println(morrisTraversalKthLargest(root, i));;
        }
    }
}
