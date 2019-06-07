package leetcode.binaryTree;

public class LargestBSTSubtree {
    public int largestBSTSubtree(TreeNode root) {
        return helper(root).size;
    }

    public Wrapper helper(TreeNode node){
        Wrapper curr = new Wrapper();

        if(node == null){
            curr.isBST= true;
            return curr;
        }

        Wrapper l = helper(node.left);
        Wrapper r = helper(node.right);

        //current subtree's boundaries
        curr.lower = Math.min(node.val, l.lower);
        curr.upper = Math.max(node.val, r.upper);

        //check left and right subtrees are BST or not
        //check left's upper again current's value and right's lower against current's value
        if(l.isBST && r.isBST && l.upper<=node.val && r.lower>=node.val){
            curr.size = l.size+r.size+1;
            curr.isBST = true;
        }else{
            curr.size = Math.max(l.size, r.size);
            curr.isBST  = false;
        }

        return curr;
    }

    class Wrapper{
        int size;
        int lower, upper;
        boolean isBST;

        public Wrapper(){
            lower = Integer.MAX_VALUE;
            upper = Integer.MIN_VALUE;
            isBST = false;
            size = 0;
        }
    }

    public int largestBSTSubtree2(TreeNode root) {
        return Math.abs(inorder(root)[0]);//0:min 1:max 2:isBST 3: nodes num 4: maxSubBST
    }

    public int[] inorder(TreeNode root){
        int[] res = {0, Integer.MAX_VALUE, Integer.MIN_VALUE};

        if(root == null) return res;
        int[] leftRes = inorder(root.left);
        int[] rightRes = inorder(root.right);

        if(leftRes[0] < 0 || rightRes[0] < 0
                || root.val <= leftRes[2] || root.val >= rightRes[1]){
            res[0] = (-1) * Math.max(Math.abs(leftRes[0]), Math.abs(rightRes[0]));
        }else{
            res[0] =  1 + leftRes[0] + rightRes[0];
            res[1] =  Math.min(root.val, leftRes[1]);
            res[2] =  Math.max(root.val, rightRes[2]);
        }
        return res;
    }
}
