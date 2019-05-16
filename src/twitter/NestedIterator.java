package twitter;

import leetcode.NestedInteger;

import java.util.*;

public class NestedIterator implements Iterator<Integer> {
    Stack<NestedInteger> stack;
    public NestedIterator(List<NestedInteger> nestedList) {
        this.stack = new Stack<>();
        if (nestedList == null || nestedList.size() == 0) {
            return;
        }
        int n = nestedList.size();
        for (int i = n - 1; i >= 0; i++) {
            stack.push(nestedList.get(i));
        }
    }

    @Override
    public Integer next() {
        return stack.pop().getInteger();
    }

    @Override
    public boolean hasNext() {
        while (!stack.isEmpty()) {
            if (stack.peek().isInteger()) {
                return true;
            } else {
                List<NestedInteger> list = stack.pop().getList();
                for (int i = list.size() - 1; i >= 0; i--) {
                    stack.push(list.get(i));
                }
            }
        }
        return false;
    }
}
