package google.vo.mianjing;

// https://leetcode.com/discuss/interview-question/413991/
// https://leetcode.com/discuss/interview-question/435536/Google-or-Onsite-or-Design-a-Cord/392031
public class Rope {
    Node root;
    public Rope(String str) {
        int size = (int) Math.sqrt(str.length());
        this.root = buildRope(str, 0, str.length(), size);
    }

    private Node buildRope(String str, int l, int r, int size) {
        int len = r - l;
        if (len <= size) {
            return new Node(true, str.substring(l, r));
        }
        int mid = l + size;
        Node left = buildRope(str, l , mid, size);
        Node right = buildRope(str,mid, r, size);
        Node root = new Node();
        root.left = left;
        root.right = right;
        root.length = len;
        return root;
    }

    public char charAt(int i) {
        if (i < 0 || i >= root.length) throw new StringIndexOutOfBoundsException();

        Node cur = root;

        return dfsFindChar(cur, i);
    }

    private char dfsFindChar(Node cur, int i) {
        if (cur.isLeafNode) {
            return cur.str.charAt(i);
        }
        Node left = cur.left, right = cur.right;
        if (i + 1 > left.length) {
            return dfsFindChar(right, i - left.length);
        } else {
            return dfsFindChar(left, i);
        }
    }

    public void delete(int start, int end) {
        if(start < 0 || start > root.length || start > end) throw new StringIndexOutOfBoundsException();
        delete(root, start, end);
    }

    private void delete(Node node, int start, int end) {
        if(start == end) return;
        if (node.isLeafNode) {
            node.str = node.str.substring(0, start) + node.str.substring(end);
            node.length = node.str.length();
            return;
        }
        Node left = node.left, right = node.right;
        if (end <= left.length) {
            delete(left, start, end);
        } else if (start > left.length - 1) {
            delete(right, start, end);
        } else {
            delete(left, start, left.length);
            delete(right, 0, end - left.length);
        }
        node.length = node.left.length + node.right.length;
    }

    public String substring(int start, int end) {
        if(start < 0 || start > root.length || start > end) throw new StringIndexOutOfBoundsException();

        return substring(root, start, end);
    }

    public String substring(Node node, int start, int end) {
        if(start == end) return "";
        if(node.isLeafNode) {
            return node.str.substring(start, end);
        }

        if(start > node.left.length - 1) {
            return substring(node.right, start - node.left.length, end - node.left.length);
        } else if(end - 1 <= node.left.length - 1){
            return substring(node.left, start, end);
        } else {
            int rightStart = end - node.left.length;
            return substring(node.left, start, node.left.length) + substring(node.right, 0, rightStart);
        }
    }

    public static void main(String[] args) {
        String s = "abcdefghijklmnopqrstuvwxyz";
        Rope rope = new Rope(s);
        for (int i = 0; i < s.length(); i++) {
            System.out.print(rope.charAt(i) + " ");
        }
        System.out.println();
        // String s = "abhijklmnopqrstuvwxyz";
        rope.delete(2,5);
        for (int i = 0; i < 15; i++) {
            System.out.print(rope.charAt(i) + " ");
        }
    }
}

class Node {
    public boolean isLeafNode;
    public Node left;
    public Node right;
    public int length;
    public String str;

    public Node(){
        this.isLeafNode = false;
    }

    public Node( boolean isLeafNode, String str) {
        this.isLeafNode = isLeafNode;
        this.str = str;
        this.length = isLeafNode ? str.length() : left.length + right.length;
    }
}