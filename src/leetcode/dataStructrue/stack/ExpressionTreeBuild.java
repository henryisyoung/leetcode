package leetcode.dataStructrue.stack;

import java.util.Stack;

public class ExpressionTreeBuild {
  public class ExpressionTreeNode {
      public String symbol;
      public ExpressionTreeNode left, right;
      int val;
      public ExpressionTreeNode(String symbol,int val) {
          this.symbol = symbol;
          this.val = val;
          this.left = this.right = null;
      }
  }
    int get(String a, Integer base) {
        if (a.equals("+") || a.equals("-"))
            return 1 + base;
        if (a.equals("*") || a.equals("/"))
            return 2 + base;

        return Integer.MAX_VALUE;
    }
    public ExpressionTreeNode build(String[] expression) {
        int base = 0;
        if (expression == null || expression.length == 0) return null;
        Stack<ExpressionTreeNode> stack = new Stack<>();
        for (String e : expression) {
            if (e.equals("(")) {
                base += 10;
                continue;
            } else if (e.equals(")")) {
                base -= 10;
                continue;
            }
            int val = get(e, base);
            ExpressionTreeNode node = new ExpressionTreeNode(e, val);
            while (!stack.isEmpty() && val <= stack.peek().val) {
                node.left = stack.pop();
            }
            if (!stack.isEmpty()) {
                stack.peek().right = node;
            }
            stack.push(node);
        }
        if (stack.isEmpty()) return null;
        ExpressionTreeNode result = stack.pop();
        while (!stack.isEmpty()) {
            result = stack.pop();
        }
        return result;
    }
}
