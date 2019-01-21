package leetcode;
import java.util.*;
public class Solution222 {
	// divide & conquer
	public int countNodes(TreeNode root) {
	    if(root==null)
	        return 0;
	 
	    int left = getLeftHeight(root)+1;    
	    int right = getRightHeight(root)+1;
	 
	    if(left==right){
	        return (2<<(left-1))-1;
	    }else{
	        return countNodes(root.left)+countNodes(root.right)+1;
	    }
	}
	 
	public int getLeftHeight(TreeNode n){
	    if(n==null) return 0;
	 
	    int height=0;
	    while(n.left!=null){
	        height++;
	        n = n.left;
	    }
	    return height;
	}
	 
	public int getRightHeight(TreeNode n){
	    if(n==null) return 0;
	 
	    int height=0;
	    while(n.right!=null){
	        height++;
	        n = n.right;
	    }
	    return height;
	}
	
	public int countNodes2(TreeNode root) {
        int LeftMostHeight=0;
        TreeNode iter=root;
        while(iter!=null){
            LeftMostHeight++;
            iter=iter.left;
        }

        int RightMostHeight=0;
        iter=root;
        while(iter!=null){
            RightMostHeight++;
            iter=iter.right;
        }        

        if(LeftMostHeight==RightMostHeight)
            return (1<<LeftMostHeight)-1;

        int left=0,right=(1<<RightMostHeight)-1;
        int leafNum=Aux(root,left,right,RightMostHeight);
        int notleafNum=(1<<RightMostHeight)-1;
        return leafNum+notleafNum;
    }
    private int Aux(TreeNode root,int left,int right,int len){
        if(left==right) return left+1;
        if(left==right-1) return left+1;

        int mid=left+((right-left)>>1);

        TreeNode midNode=root;
        for(int i=0;i<len;i++)
            midNode=(mid>>(len-i-1)&1)==0?midNode.left:midNode.right;

        if(midNode==null)
            return Aux(root,left,mid,len);
        else
            return Aux(root,mid,right,len);
    }
    
    public int countNodesBS(TreeNode root) {
        if(root == null) return 0;
        int n = getLeftHeight(root);
        int left = (int) Math.pow(2, n - 1), right = (int) (Math.pow(2, n) - 1);
        while(left + 1 < right){
        	int mid = left + (right - left)/2;
        	if(getNode(root, mid)){
        		left = mid;
        	}else{
        		right = mid;
        	}
        }
        if(getNode(root, right)) return right;
        return left;
    }

	private boolean getNode(TreeNode root, int n) {
		Stack<Integer> stack = new Stack<Integer>();
		while(n > 1){
			stack.push(n % 2);
			n /= 2;
		}
		while(!stack.isEmpty()){
			int i = stack.pop();
			if(i == 0) {
				root = root.left;
			}else{
				root = root.right;
			}
			if(root == null) return false;
		}
		return true;
	}
}
