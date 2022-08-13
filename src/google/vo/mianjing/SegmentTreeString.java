package google.vo.mianjing;

public class SegmentTreeString {
    class Node{
        int start, end;
        Node left, right;
        String str;
        boolean isLeaf;

        public Node(int start, int end) {
            this.start = start;
            this.end = end;
            this.isLeaf = false;
        }

        public Node(int start, int end, String str) {
            this.start = start;
            this.end = end;
            this.str = str;
            this.isLeaf = true;
        }
    }

    Node root;
    public SegmentTreeString(String s) {
        this.root = buildTree(s, 0, s.length() - 1);
    }

    private Node buildTree(String str, int start, int end) {
        if (start > end) {
            return null;
        }
        if (start == end) {
            return new Node(start, end, str.charAt(start) + "");
        }
        int mid = start + (end - start) / 2;
        Node left = buildTree(str, start, mid);
        Node right = buildTree(str, mid + 1, end);
        Node root = new Node(start, end);
        root.left = left;
        root.right = right;
        return root;
    }

    public String find(int pos) {
        Node node = root;
        return findFrom(node, pos);
    }

    private String findFrom(Node node, int pos) {
        if (node.isLeaf && node.start == pos) {
            return node.str;
        }
        int mid = node.start + (node.end - node.start) / 2;
        if (pos > mid) {
            return findFrom(node.right, pos);
        } else {
            return findFrom(node.left, pos);
        }
    }

    public static void main(String[] args) {
        String s = "abcdefg";
        SegmentTreeString tree = new SegmentTreeString(s);
        for(int i = 0 ; i < s.length(); i++) {
            System.out.println(tree.find(i));
        }
    }
}
