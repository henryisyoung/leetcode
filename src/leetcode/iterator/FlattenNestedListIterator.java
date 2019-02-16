package leetcode.iterator;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

interface NestedInteger {

    // @return true if this NestedInteger holds a single integer, rather than a nested list.
    public boolean isInteger();

    // @return the single integer that this NestedInteger holds, if it holds a single integer
    // Return null if this NestedInteger holds a nested list
    public Integer getInteger();

    // @return the nested list that this NestedInteger holds, if it holds a nested list
    // Return null if this NestedInteger holds a single integer
    public List<NestedInteger> getList();
}

public class FlattenNestedListIterator implements Iterator<Integer> {
    private Stack<NestedInteger> stack;
    public FlattenNestedListIterator(List<NestedInteger> nestedList) {
        if (nestedList == null) {
            return;
        }
        for (int i = nestedList.size() - 1; i >= 0; i--) {
            stack.push(nestedList.get(i));
        }
    }

    @Override
    public Integer next() {
        return stack.pop().getInteger();
    }

    @Override
    public void remove() {

    }

    @Override
    public boolean hasNext() {
        while (!stack.isEmpty()) {
            if (stack.peek().isInteger()) {
                return true;
            } else {
                List<NestedInteger> nestedInteger = stack.pop().getList();
                for (int i = nestedInteger.size() - 1; i >= 0; i--) {
                    stack.push(nestedInteger.get(i));
                }
            }
        }
        return false;
    }
}

