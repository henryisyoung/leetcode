package leetcode;
import java.util.*;
public class NestedIterator implements Iterator<Integer> {
    Stack<NestedInteger> stack = new Stack<NestedInteger>();;
    public NestedIterator(List<NestedInteger> nestedList) {
        if (nestedList.size() == 0 || nestedList == null) {
            return;
        }
        int n = nestedList.size();
        for (int i = n - 1; i >= 0; i--) {
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
    	    NestedInteger cur = stack.peek();
    	    if (cur.isInteger()) {
    	        return true;
            } else {
    	        stack.pop();
                List<NestedInteger> list = cur.getList();
    	        for (int i = list.size() - 1; i >= 0; i--) {
    	            stack.push(list.get(i));
                }
            }
        }
        return false;
    }
}
