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

    public int depthSumInverse(List<NestedInteger> nestedList) {
        int maxDepth = findMaxDepth(nestedList);

        return calculateSum(nestedList, maxDepth);
    }

    private int findMaxDepth(List<NestedInteger> nestedList) {
        int depth = 1;
        for(NestedInteger item : nestedList) {
            if(!item.isInteger()) {
                depth = Math.max(depth, findMaxDepth(item.getList()) + 1);
            }
        }
        return depth;
    }

    private int calculateSum(List<NestedInteger> nestedList, int depth) {
        int sum = 0;
        for(NestedInteger item : nestedList) {
            if(item.isInteger()) {
                sum += depth * item.getInteger();
            } else {
                sum += calculateSum(item.getList(), depth - 1);
            }
        }

        return sum;
    }

    public int depthSum(List<NestedInteger> nestedList) {
        int sum = 0;
        if (nestedList == null || nestedList.size() == 0) return sum;
        Queue<NestedInteger> queue = new LinkedList<>();
        queue.addAll(nestedList);
        int level = 1;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                NestedInteger cur = queue.poll();
                if (cur.isInteger()) {
                    sum += cur.getInteger() * level;
                } else {
                    queue.addAll(cur.getList());
                }
            }
            level++;
        }
        return sum;
    }
}
