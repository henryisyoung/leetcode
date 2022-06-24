package apple;

import leetcode.solution.NestedInteger;

import java.util.List;

public class NestedListWeightSumII {
    public int depthSumInverse(List<NestedInteger> nestedList) {
        if(nestedList == null || nestedList.size() == 0) {
            return 0;
        }
        int maxDepth = findMaxDepth(nestedList);
        return depthSumInverseHelper(nestedList, maxDepth);
    }

    private int depthSumInverseHelper(List<NestedInteger> nestedList, int depth) {
        if(nestedList == null || nestedList.size() == 0) {
            return 0;
        }
        int sum = 0;
        for (NestedInteger ni : nestedList) {
            if (ni.isInteger()) {
                sum += ni.getInteger() * depth;
            } else {
                sum += depthSumInverseHelper(ni.getList(), depth - 1);
            }
        }
        return sum;
    }

    private int findMaxDepth(List<NestedInteger> nestedList) {
        if(nestedList == null || nestedList.size() == 0) {
            return 0;
        }
        int max = 1;
        for (NestedInteger ni : nestedList) {
            if (!ni.isInteger()) {
                max = Math.max(max, 1 + findMaxDepth(ni.getList()));
            }
        }
        return max;
    }
}
