package Bloomberg;
import java.util.*;
public class BinaryTreePathsWithSum {
	public static void main(String[] args) {
        TreeNode a = new TreeNode(5);
        TreeNode b = new TreeNode(1);
        TreeNode c = new TreeNode(2);
        TreeNode d = new TreeNode(2);
        TreeNode e = new TreeNode(5);
        TreeNode f = new TreeNode(6);
        //TreeNode g = new TreeNode(5);

        TreeNode root = a;
        a.left = b;
        a.right = c;
        b.left = d;
        b.right = e;
        c.right = f;
        BinaryTreePathsWithSum t = new BinaryTreePathsWithSum(); 
        System.out.println(t.printPathsForValue(8, root));
        
	}
        /*
                5
              /   \
             1     2
            / \     \
           2    5    6
        */

    
    void helper(List<String> list, String s, TreeNode origin, int sum, int value){
        if(origin == null)return;
        sum+=origin.val;
        if(sum==value){String add = s;add+=" "+origin.val; list.add(add);} 
        helper(list, s+" "+origin.val, origin.left, sum, value);
        helper(list, s+" "+origin.val, origin.right, sum ,value);   
    }
    
    void checkPaths(TreeNode root, List<String> list, int value){ 
        if(root == null)return;  
        helper(list,"", root, 0, value);   
        checkPaths(root.left, list, value);
        checkPaths(root.right, list, value); 
    }
    
    public List<String> printPathsForValue(int value, TreeNode root){
        List<String> paths = new ArrayList<>();
        checkPaths(root, paths, value);
        return paths;
    }
}
