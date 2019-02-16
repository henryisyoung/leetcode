package leetcode.iterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FlattenList {
    public List<Integer> flatten(List<NestedInteger> nestedList) {
        List<Integer> result = new ArrayList<>();
        if (nestedList == null || nestedList.size() == 0) {
            return result;
        }
        Stack<NestedInteger> stack = new Stack<>();
        for (int i = nestedList.size() - 1; i >= 0; i--) {
            stack.push(nestedList.get(i));
        }
        while (!stack.isEmpty()) {
            if (stack.peek().isInteger()) {
                result.add(stack.pop().getInteger());
            } else {
                List<NestedInteger> nestedIntegers = stack.pop().getList();
                for (int i = nestedIntegers.size() - 1; i >= 0; i--) {
                    stack.push(nestedIntegers.get(i));
                }
            }
        }
        return result;
    }
}
