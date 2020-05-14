package facebook;

import Bloomberg.TreeNode;

public class TernaryTree {
    public TreeNode ternary(String s) {
        if (s == null || s.length() == 0) return null;
        if (s.charAt(0) == '(') s = s.substring(1, s.length());
        char a = s.charAt(0);

        int pos = findPos(s);
        TreeNode left = ternary(s.substring(2,pos)), right = ternary(s.substring(pos + 1));
        TreeNode root = new TreeNode(a);
        root.left = left;
        root.right = right;
        return root;
    }

    private int findPos(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                count++;
            }
            if (s.charAt(i) == ')') {
                count--;
            }
            if (count == 0 && s.charAt(i) == ':') {
                return i;
            }
        }
        return -1;
    }
}
