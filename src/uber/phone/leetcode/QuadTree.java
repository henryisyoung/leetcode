package uber.phone.leetcode;

public class QuadTree {
    class Node {
        public Integer val;
        public boolean isLeaf;
        public Node topLeft;
        public Node topRight;
        public Node bottomLeft;
        public Node bottomRight;

        public Node() {}

        public Node(Integer _val,boolean _isLeaf,Node _topLeft,Node _topRight,Node _bottomLeft,Node _bottomRight) {
            val = _val;
            isLeaf = _isLeaf;
            topLeft = _topLeft;
            topRight = _topRight;
            bottomLeft = _bottomLeft;
            bottomRight = _bottomRight;
        }
    };

    public Node construct(int[][] grid) {
        return constructHelper(grid, 0, 0, grid.length);
    }

    private Node constructHelper(int[][] grid, int r, int c, int length) {
        if (length == 1) {
            return new Node(grid[r][c], true, null, null, null, null);
        }
        int len = length / 2;
        Node tl = constructHelper(grid, r, c, len);
        Node tr = constructHelper(grid, r, c  + len, len);
        Node bl = constructHelper(grid, r + len, c, len);
        Node br = constructHelper(grid, r + len, c + len, len);
        if (tl.isLeaf && tr.isLeaf && bl.isLeaf && br.isLeaf && tl.val == tr.val && tr.val == br.val && br.val == bl.val && bl.val == tl.val) {
            return new Node(tl.val, true, null, null, null, null);
        }
        return new Node(null, false, tl, tr, bl, br);
    }

}
