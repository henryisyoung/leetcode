package leetcode.dataStructrue.stack;

import java.util.Stack;

public class ExpressionTreeBuild {
  public class ExpressionTreeNode {
      public String symbol;
      public ExpressionTreeNode left, right;
      public ExpressionTreeNode(String symbol) {
          this.symbol = symbol;
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
        return null;
    }
}
