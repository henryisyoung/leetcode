package uber;

public class QuadTree {
    class Node {
        public boolean val;
        public boolean isLeaf;
        public Node topLeft;
        public Node topRight;
        public Node bottomLeft;
        public Node bottomRight;

        public Node() {}

        public Node(boolean _val,boolean _isLeaf,Node _topLeft,Node _topRight,Node _bottomLeft,Node _bottomRight) {
            val = _val;
            isLeaf = _isLeaf;
            topLeft = _topLeft;
            topRight = _topRight;
            bottomLeft = _bottomLeft;
            bottomRight = _bottomRight;
        }
    }

    public Node construct(int[][] grid) {
        return build(grid, 0, 0, grid.length);
    }

    private Node build(int[][] grid, int i, int j, int size) {
        if (size == 1) return new Node(grid[i][j] == 1, true, null, null, null, null);

        int h = size / 2;
        Node tl = build(grid, i, j, h), tr = build(grid, i, j + h, h),
                bl = build(grid, i + h, j, h), br = build(grid, i + h, j + h, h);
        if (tl.isLeaf && bl.isLeaf && tr.isLeaf && br.isLeaf && tl.val == bl.val && tl.val == tr.val && tl.val == br.val) {
            return new Node(tl.val, true, tl, tr, bl, br);
        }
        return new Node(false, false, tl, tr, bl, br);
    }
}
