package Bloomberg;

public class ChangeTreeToDoubleLinkedList {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public TreeNode changeTreeToDLL(TreeNode root){
		if(root == null){
			return root;
		}
		TreeNode node = helper(root);
		while(node.left != null){
			node = node.left;
		}
		return node;
	}

	private TreeNode helper(TreeNode root) {
		if(root == null){
			return root;
		}
		if(root.left != null){
			TreeNode left = helper(root.left);
			while(left.right != null){
				left = left.right;
			}
			left.right = root;
			root.left = left;
		}
		if(root.right != null){
			TreeNode right = helper(root.right);
			while(right.left != null){
				right = right.left;
			}
			right.left = root;
			root.right = right;
		}
		return root;
	}
	
	
	TreeNode root;
    
    // head --> Pointer to head node of created doubly linked list
	TreeNode head;
      
    // Initialize previously visited node as NULL. This is
    // static so that the same value is accessible in all recursive
    // calls
    TreeNode prev = null;
	public void BinaryTree2DoubleLinkedList(TreeNode root) 
    {
        // Base case
        if (root == null)
            return;
  
        // Recursively convert left subtree
        BinaryTree2DoubleLinkedList(root.left);
  
        // Now convert this node
        if (prev == null) 
            head = root;
        else
        {
            root.left = prev;
            prev.right = root;
        }
        prev = root;
  
        // Finally convert right subtree
        BinaryTree2DoubleLinkedList(root.right);
    }
}
