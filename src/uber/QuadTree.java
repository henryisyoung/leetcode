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
        return builder(0, 0, grid, grid.length);
    }

    private Node builder(int i, int j, int[][] grid, int length) {
        if (length == 1) return new Node(grid[i][j] == 1, true, null, null, null, null);
        int h = length / 2;
        Node lt = builder(i, j, grid, h), lb = builder(i + h, j, grid, h),
                rt = builder(i, j + h, grid, h), rb = builder(i + h, j + h, grid, h);
        if (lt.isLeaf && lb.isLeaf && rt.isLeaf && rb.isLeaf && lb.val == lt.val && lt.val == rb.val && rt.val == lb.val) {
            return new Node(lt.val, true, null, null, null, null);
        }
        return new Node(false, false, lt, rt, lb, rb);
    }
}
