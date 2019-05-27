package google;

public class PostOrderFindFather {
    // https://www.1point3acres.com/bbs/thread-522935-1-1.html
    public int findParent(int n, int x) {
        if(n == 1) return -1;
        if(x < 3) return 3;
        if(x == (1 << n) - 1) return -1; // root

        // If x is the left or right child of the root
        if(x == (1 << (n - 1)) - 1 || x == (1 << n) - 2) {
            return (1 << n) - 1;
        }
        if(x < (1 << (n - 1))) {
            // In left subtree but not the left child
            return findParent(n - 1, x);
        } else {
            // In right subtree but not the right child
            return (1 << n - 1) - 1 + findParent(n - 1, x - (1 << (n - 1)) + 1);
        }
    }

    private int findNode (int target, int left, int right, int parent) {
        if (target == right) {
            return parent;
        }
        if (target < (left + right) / 2) {
            return findNode(target, left, (left + right)/2 -1, right);
        }
        return findNode(target, (left + right)/2, right -1, right);
    }

}
