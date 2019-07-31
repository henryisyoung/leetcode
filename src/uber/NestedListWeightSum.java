package uber;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class NestedListWeightSum {

    // This is the interface that allows for creating nested lists.
    // You should not implement it, or speculate about its implementation
    public interface NestedInteger {

     // @return true if this NestedInteger holds a single integer,
     // rather than a nested list.
     public boolean isInteger();

     // @return the single integer that this NestedInteger holds,
     // if it holds a single integer
     // Return null if this NestedInteger holds a nested list
     public Integer getInteger();

     // @return the nested list that this NestedInteger holds,
     // if it holds a nested list
     // Return null if this NestedInteger holds a single integer
     public List<NestedInteger> getList();
    }

    public int depthSum(List<NestedInteger> nestedList) {
        int sum = 0;
        if (nestedList == null || nestedList.size() == 0) return sum;
        Queue<NestedInteger> queue = new LinkedList<>();
        int depth = 1;
        queue.addAll(nestedList);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                NestedInteger cur = queue.poll();
                if (cur.isInteger()) {
                    sum += cur.getInteger() * depth;
                } else {
                    queue.addAll(cur.getList());
                }
            }
            depth++;
        }
        return sum;
    }
}
